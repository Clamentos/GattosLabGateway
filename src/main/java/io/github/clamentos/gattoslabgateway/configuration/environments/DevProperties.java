package io.github.clamentos.gattoslabgateway.configuration.environments;

///
import io.github.clamentos.gattoslabgateway.configuration.ApplicationProperties;

///..
import java.time.Duration;

///..
import lombok.Getter;

///
@Getter

///
public final class DevProperties extends ApplicationProperties {

    ///
    private final Environment currentEnvironment;

    ///..
    private final int serverPort;
    private final int socketQueueSize;
    private final String sslKeystoreName;
    private final String sslKeystorePassword;
    private final Duration serverStopTimeout;

    ///..
    private final Duration batchSchedulerShutdownTimeout;

    ///..
    private final String dynamicPropertiesRefreshSchedule;
    private final String dynamicPropertiesPath;

    ///..
    private final String requestMetricsFilePath;
    private final int requestMetricsBufferSize;

    ///..
    private final String systemMetricsFilePath;
    private final String systemMetricsPollSchedule;

    ///..
    private final String logsSquashSchedule;

    ///..
    private final String observabilitySendSchedule;

    ///
    public DevProperties() {

        currentEnvironment = Environment.DEV;

        serverPort = 8443;
        socketQueueSize = 0;
        sslKeystoreName = "keystore.p12";
        sslKeystorePassword = "password";
        serverStopTimeout = Duration.ofSeconds(4);

        batchSchedulerShutdownTimeout = Duration.ofSeconds(4);

        dynamicPropertiesRefreshSchedule = "1m";
        dynamicPropertiesPath = "./observability/dynamic_properties.conf";

        requestMetricsFilePath = "./observability/request_metrics.log";
        requestMetricsBufferSize = 8192;

        systemMetricsFilePath = "./observability/system_metrics.log";
        systemMetricsPollSchedule = "s5";

        logsSquashSchedule = "m1";

        observabilitySendSchedule = "m1";
    }

    ///
}
