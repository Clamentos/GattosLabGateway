package io.github.clamentos.gattoslabgateway.observability;

///
import com.sun.net.httpserver.HttpExchange;

///..
import io.github.clamentos.gattoslabgateway.configuration.ApplicationProperties;
import io.github.clamentos.gattoslabgateway.configuration.Constants;
import io.github.clamentos.gattoslabgateway.observability.logging.Logger;
import io.github.clamentos.gattoslabgateway.observability.logging.entities.LogEvent;
import io.github.clamentos.gattoslabgateway.observability.metrics.contexts.SiphonContext;
import io.github.clamentos.gattoslabgateway.observability.metrics.contexts.SystemMetricsContext;
import io.github.clamentos.gattoslabgateway.observability.metrics.entities.RequestMetricsEntity;
import io.github.clamentos.gattoslabgateway.observability.metrics.entities.SystemMetricsEntity;
import io.github.clamentos.gattoslabgateway.scheduling.BatchScheduler;
import io.github.clamentos.gattoslabgateway.utils.GenericUtils;

///..
import java.io.BufferedReader;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

///
public final class ObservabilityService {

    ///
    private final Logger logger;
    private final HttpClient httpClient;

    ///..
    private final ObservabilityFile<LogEvent> logFile;
    private final ObservabilityFile<RequestMetricsEntity> requestMetricsFile;
    private final ObservabilityFile<SystemMetricsEntity> systemMetricsFile;

    ///..
    private final SystemMetricsContext systemMetricsContext;

    ///..
    private final SiphonContext<RequestMetricsEntity> primaryMetricsContext;
    private final SiphonContext<RequestMetricsEntity> secondaryMetricsContext;
    private final AtomicReference<SiphonContext<RequestMetricsEntity>> metricsContextReference;

    ///
    public ObservabilityService(

        final ApplicationProperties applicationProperties,
        final HttpClient httpClient,
        final ObservabilityFile<LogEvent> logFile,
        final BatchScheduler batchScheduler

    ) throws IllegalArgumentException {

        logger = new Logger();
        this.httpClient = httpClient;

        this.logFile = logFile;
        requestMetricsFile = new ObservabilityFile<>(applicationProperties.getRequestMetricsFilePath());
        systemMetricsFile = new ObservabilityFile<>(applicationProperties.getSystemMetricsFilePath());

        final String schedule = applicationProperties.getSystemMetricsPollSchedule();
        final long period = batchScheduler.schedule(this::pollSystemMetricsTask, "gattos-lab-gateway-system-metrics-poller", schedule);
        systemMetricsContext = new SystemMetricsContext(period);

        final int siphonSize = applicationProperties.getRequestMetricsBufferSize();
        primaryMetricsContext = new SiphonContext<>(this::drainSiphonTask, siphonSize, RequestMetricsEntity::new);
        secondaryMetricsContext = new SiphonContext<>(this::drainSiphonTask, siphonSize, RequestMetricsEntity::new);
        metricsContextReference = new AtomicReference<>(primaryMetricsContext);

        batchScheduler.schedule(this::fileDumpTask, "gattos-lab-gateway-observability-sender", applicationProperties.getObservabilitySendSchedule());
    }

    ///
    public void requestStarted() {

        systemMetricsContext.requestStarted();
    }

    ///..
    public void requestEnded(final HttpExchange exchange) {

        while(!(metricsContextReference.get().update(entity -> this.updateRequestMetrics(entity, exchange)))) {

            GenericUtils.silentSleep(1L);
        }
    }

    ///.
    private void updateRequestMetrics(final RequestMetricsEntity requestMetricsEntity, final HttpExchange exchange) {

        final long startTime = (long)exchange.getAttribute("START_TIME");

        requestMetricsEntity.setTimestamp(startTime);
        requestMetricsEntity.setId((long)exchange.getAttribute("REQUEST_ID"));
        requestMetricsEntity.setLatency((int)(System.currentTimeMillis() - startTime));
        requestMetricsEntity.setPath(exchange.getRequestURI().getPath());
        requestMetricsEntity.setUserAgent((String)exchange.getAttribute("USER_AGENT"));
        requestMetricsEntity.setUnknown((boolean)exchange.getAttribute("IS_UNKNOWN"));
        requestMetricsEntity.setHttpStatus((short)exchange.getResponseCode());
    }

    ///..
    private void fileDumpTask() {

        this.drainSiphonTask(metricsContextReference.get().drainSiphon());

        this.dumpFile(logFile);
        this.dumpFile(requestMetricsFile);
        this.dumpFile(systemMetricsFile);
    }

    ///..
    private void drainSiphonTask(final List<RequestMetricsEntity> requestMetricsEntities) {

        if(requestMetricsEntities != null && !requestMetricsEntities.isEmpty()) {

            final SiphonContext<RequestMetricsEntity> previousContext = this.swapContexts();
            while(!previousContext.isNoOneThere()) GenericUtils.silentSleep(1L);

            try { requestMetricsFile.write(requestMetricsEntities); }
            catch(final IOException exc) { logger.error("Could not write to request metrics file because", exc); }

            previousContext.reset();
        }
    }

    ///..
    private void pollSystemMetricsTask() {

        if(systemMetricsContext != null) {

            final SystemMetricsEntity entity = systemMetricsContext.sample();

            if(entity != null) {

                try { systemMetricsFile.write(List.of(entity)); }
                catch(final IOException exc) { logger.error("Could not write to system metrics file because", exc); }
            }
        }
    }

    ///..
    private void dumpFile(final ObservabilityFile<?> observabilityFile) {

        try(final BufferedReader reader = observabilityFile.createReaderAndLock()) {

            final StringBuilder body = new StringBuilder();

            boolean error = false;
            int counter = 0;
            String line;

            while((line = reader.readLine()) != null) {

                if(!line.isBlank()) {

                    body.append(line).append("\n");
                    counter++;
                }

                if(counter == Constants.OBSERVABILITY_FILE_SEND_CHUNK_SIZE) {

                    final HttpRequest request = HttpRequest

                        .newBuilder()
                        .uri(URI.create("..."))
                        .POST(BodyPublishers.ofString(body.toString()))
                        .build()
                    ;

                    final HttpResponse<String> response = httpClient.send(request, BodyHandlers.ofString());

                    if(response.statusCode() != 200) {

                        logger.error("Request error while sending observability data to backend: " + response.statusCode() + ", " + response.body());
                        error = true;

                        break;
                    }

                    counter = 0;
                }
            }

            if(!error) observabilityFile.clear();
        }

        catch(final IOException | InterruptedException | RuntimeException exc) {

            if(exc instanceof InterruptedException) Thread.currentThread().interrupt();
            logger.error("Could not send observability data to backend because", exc);
        }

        observabilityFile.unlock();
    }

    ///..
    private SiphonContext<RequestMetricsEntity> swapContexts() {

        if(metricsContextReference.compareAndSet(primaryMetricsContext, secondaryMetricsContext)) return primaryMetricsContext;
        metricsContextReference.set(primaryMetricsContext);

        return secondaryMetricsContext;
    }

    ///
}
