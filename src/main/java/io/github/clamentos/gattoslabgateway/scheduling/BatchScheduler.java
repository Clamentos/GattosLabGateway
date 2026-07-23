package io.github.clamentos.gattoslabgateway.scheduling;

///
import io.github.clamentos.gattoslabgateway.configuration.ApplicationProperties;
import io.github.clamentos.gattoslabgateway.configuration.Constants;
import io.github.clamentos.gattoslabgateway.observability.logging.Logger;
import io.github.clamentos.gattoslabgateway.utils.GenericUtils;

///..
import java.io.Closeable;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicBoolean;

///
public final class BatchScheduler implements Closeable {

    ///
    private final Logger logger;
    private final Logger cronLogger;

    ///..
    private final Duration shutdownTimeout;

    ///..
    private final Thread scheduler;

    ///..
    private final List<SimpleCron> jobs;
    private final Map<Long, Thread> workers;

    ///..
    private final AtomicBoolean halt;

    ///
    public BatchScheduler(final ApplicationProperties applicationProperties) {

        logger = new Logger();
        cronLogger = new Logger(SimpleCron.class.getSimpleName());

        shutdownTimeout = applicationProperties.getBatchSchedulerShutdownTimeout();

        jobs = new CopyOnWriteArrayList<>();
        scheduler = GenericUtils.spawnVirtualThread("gattos-lab-gateway-batch-scheduler", this::triggerJobs);

        workers = new ConcurrentHashMap<>();

        halt = new AtomicBoolean();
    }

    ///
    @Override
    public void close() {

        logger.info("Begin shutdown...");

        try {

            halt.set(true);
            if(!scheduler.join(shutdownTimeout.multipliedBy(Math.max(workers.size(), 1)))) logger.warn("Timed-out while joining");
        }

        catch(final InterruptedException _) {

            logger.error("Interrupted wile joining, force quitting");
            Thread.currentThread().interrupt();
        }

        logger.info("End shutdown");
    }

    ///..
    public long schedule(final Runnable task, final String name, final String simpleCron) throws IllegalArgumentException {

        final SimpleCron cron = new SimpleCron(task, name, simpleCron, cronLogger);
        jobs.add(cron);

        logger.info("Scheduled task '" + name + "'', period: " + cron.getPeriod() + "ms");
        return cron.getPeriod();
    }

    ///.
    private final void triggerJobs() {

        final long[] idRef = new long[]{0};

        while(!halt.get()) {

            final long now = System.currentTimeMillis();
            for(final SimpleCron job : jobs) job.trigger(now, idRef, workers);

            GenericUtils.silentSleep(Constants.BATCH_SCHEDULER_SLEEP);
        }

        logger.info("Exiting, joining " + workers.size() + " workers");

        for(final Thread worker : workers.values()) {

            try {

                logger.info("Joining '" + worker.getName() + "'");
                if(!worker.join(shutdownTimeout)) logger.warn("Timed-out while joining '" + worker.getName() + "'");
            }

            catch(final InterruptedException _) {

                logger.error("Interrupted wile joining, force quitting");
                Thread.currentThread().interrupt();

                break;
            }
        }
    }

    ///
}
