package io.github.clamentos.gattoslabgateway.scheduling;

///
import io.github.clamentos.gattoslabgateway.observability.logging.Logger;
import io.github.clamentos.gattoslabgateway.utils.GenericUtils;

///..
import java.util.Map;

///..
import lombok.Getter;

///
public final class SimpleCron {

    ///
    private static final String PREFIX = "SimpleCron.decodePeriod :: ";

    ///.
    private final Logger logger;

    ///..
    private final Runnable task;
    private final String name;

    ///..
    @Getter private final long period;
    private long nextTrigger;

    ///
    public SimpleCron(final Runnable task, final String name, final String simpleCron, final Logger logger) throws IllegalArgumentException {

        /*
            Very simple cron scheduling (no offsets): <time-unit><amount>

            time-units:

                s -> seconds
                m -> minutes
                h -> hours

            scheduling uncertainty: +- 200ms (depends how fast the scheduler thread spins)
        */

        this.logger = logger;

        period = this.decodePeriod(simpleCron);
        this.task = task;
        this.name = name;

        final long now = System.currentTimeMillis();
        nextTrigger = now + period - (now % period);
    }

    ///..
    public Thread trigger(final long timestamp, final long[] idRef, final Map<Long, Thread> workers) {

        if(timestamp >= nextTrigger) {

            final long id = idRef[0];
            nextTrigger += period;

            final Thread worker = GenericUtils.createVirtualThread("gattos-lab-gateway-batch-scheduler-worker-" + id + "-" + name, () -> {

                try { task.run(); }
                catch(final RuntimeException exc) { logger.error("Uncaught exception in scheduled task '" + name + "'", exc); }

                workers.remove(id);
            });

            workers.put(id, worker);
            worker.start();
            idRef[0]++;

            return worker;
        }

        return null;
    }

    ///.
    private long decodePeriod(final String simpleCron) throws IllegalArgumentException {

        if(simpleCron.length() >= 2) {

            final char unit = simpleCron.charAt(0);

            final long amount = Long.parseLong(simpleCron.substring(1));
            if(amount <= 0) throw new IllegalArgumentException(PREFIX + "Amount must be greater than 0");

            switch(unit) {

                case 's': return amount * 1000;
                case 'm': return amount * 1000 * 60;
                case 'h': return amount * 1000 * 60 * 60;

                default: throw new IllegalArgumentException(PREFIX + "Unknown time unit '" + unit + "'");
            }
        }

        else {

            throw new IllegalArgumentException(PREFIX + "Malformed cron expression '" + simpleCron + "'");
        }
    }

    ///
}
