package io.github.clamentos.gattoslabgateway.observability.logging.squashing;

///
import io.github.clamentos.gattoslabgateway.configuration.ApplicationProperties;
import io.github.clamentos.gattoslabgateway.observability.logging.Logger;
import io.github.clamentos.gattoslabgateway.scheduling.BatchScheduler;

///..
import java.io.Closeable;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

///
public final class SquashedLogsContainer implements Closeable {

    ///
    private final Logger logger;

    ///..
    private final Map<SquashLogEventType, LogSquasher> squashEvents;

    ///
    public SquashedLogsContainer(

        final ApplicationProperties applicationProperties,
        final BatchScheduler batchScheduler,
        final List<LogSquasher> squashEvents

    ) throws IllegalArgumentException {

        logger = new Logger();

        this.squashEvents = new EnumMap<>(SquashLogEventType.class);
        for(final LogSquasher squashEvent : squashEvents) this.squashEvents.put(squashEvent.getEventType(), squashEvent);

        batchScheduler.schedule(this::log, "SquashedLogContainer::log", applicationProperties.getLogsSquashSchedule());
    }

    ///
    @Override
    public void close() {

        logger.info("Begin shutdown...");
        this.log();
        logger.info("End shutdown");
    }

    ///.
    public void squash(final SquashLogEventType eventType, final String value) {

        final LogSquasher logSquasher = squashEvents.get(eventType);
        logSquasher.update(value);
    }

    ///.
    private void log() {

        for(final LogSquasher event : squashEvents.values()) {

            for(final String log : event.composeLogs()) logger.warn(log);
            event.reset();
        }
    }

    ///
}
