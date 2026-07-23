package io.github.clamentos.gattoslabgateway.observability.logging;

///
import io.github.clamentos.gattoslabgateway.configuration.Constants;
import io.github.clamentos.gattoslabgateway.observability.ObservabilityFile;
import io.github.clamentos.gattoslabgateway.observability.logging.entities.LogEvent;
import io.github.clamentos.gattoslabgateway.observability.logging.entities.LogSeverity;
import io.github.clamentos.gattoslabgateway.observability.metrics.contexts.SiphonContext;
import io.github.clamentos.gattoslabgateway.utils.GenericUtils;

///..
import java.io.Closeable;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;

///
public final class LoggerRoot implements Closeable {

    ///
    private static LoggerRoot INSTANCE;

    ///
    public static synchronized LoggerRoot getInstance() {

        if(INSTANCE == null) INSTANCE = new LoggerRoot();
        return INSTANCE;
    }

    ///
    private final ObservabilityFile<LogEvent> logFile;
    private final SiphonContext<LogEvent> primaryLogEventContext;
    private final SiphonContext<LogEvent> secondaryLogEventContext;
    private final AtomicReference<SiphonContext<LogEvent>> contextReference;
    private final AtomicLong logEventIdCounter;

    ///..
    private final Thread drainWorker;

    ///
    private LoggerRoot() {

        logFile = new ObservabilityFile<>("./observability/logs/gattos_lab_gateway.log");
        primaryLogEventContext = new SiphonContext<>(this::drainSiphonTask, Constants.LOG_SIPHON_SIZE, LogEvent::new);
        secondaryLogEventContext = new SiphonContext<>(this::drainSiphonTask, Constants.LOG_SIPHON_SIZE, LogEvent::new);
        contextReference = new AtomicReference<>(primaryLogEventContext);
        logEventIdCounter = new AtomicLong();

        drainWorker = GenericUtils.spawnVirtualThread("gattos-lab-gateway-logfile-cleaner", this::forceDrainSiphonTask);
    }

    ///
    @Override
    public void close() {

        try {

            drainWorker.interrupt();
            drainWorker.join(Constants.LOG_WORKER_JOIN_TIMEOUT);
        }

        catch(final InterruptedException _) {

            Thread.currentThread().interrupt();
        }
    }

    ///..
    public void info(final String logger, final String message) {

        this.log(LogSeverity.INFO, logger, message, null);
    }

    ///..
    public void warn(final String logger, final String message) {

        this.log(LogSeverity.WARNING, logger, message, null);
    }

    ///..
    public void error(final String logger, final String message, final Throwable exception) {

        this.log(LogSeverity.ERROR, logger, message, exception);
    }

    ///.
    private void log(final LogSeverity logSeverity, final String logger, final String message, final Throwable exception) {

        if(Constants.LOG_SEVERITY.ordinal() >= logSeverity.ordinal()) {

            while(!(contextReference.get().update(entity -> this.updateLogEvent(entity, logSeverity, logger, message, exception)))) {

                GenericUtils.silentSleep(1L);
            }
        }
    }

    ///..
    private void updateLogEvent(

        final LogEvent event,
        final LogSeverity logSeverity,
        final String logger,
        final String message,
        final Throwable exception
    ) {

        event.setTimestamp(System.currentTimeMillis());
        event.setSeverity(logSeverity);
        event.setId(logEventIdCounter.getAndIncrement());
        event.setThread(Thread.currentThread().getName());
        event.setLogger(logger);
        event.setMessage(message);
        event.setException(exception);
    }

    ///..
    private void drainSiphonTask(final List<LogEvent> logEvents) {

        if(logEvents != null && !logEvents.isEmpty()) {

            final SiphonContext<LogEvent> previousContext = this.swapContexts();
            while(!previousContext.isNoOneThere()) GenericUtils.silentSleep(1L);

            try { logFile.write(logEvents); }
            catch(final IOException exc) { this.error(LoggerRoot.class.getSimpleName(), "Could not write to file, because", exc); }

            previousContext.reset();
        }
    }

    ///..
    private void forceDrainSiphonTask() {

        while(true) {

            this.drainSiphonTask(contextReference.get().drainSiphon());
            if(Thread.currentThread().isInterrupted()) break;

            GenericUtils.silentSleep(Constants.LOG_DRAIN_SIPHON_TASK_PERIOD);
        }

        this.drainSiphonTask(contextReference.get().drainSiphon());
    }

    ///..
    private SiphonContext<LogEvent> swapContexts() {

        if(contextReference.compareAndSet(primaryLogEventContext, secondaryLogEventContext)) return primaryLogEventContext;
        contextReference.set(primaryLogEventContext);

        return secondaryLogEventContext;
    }

    ///
}
