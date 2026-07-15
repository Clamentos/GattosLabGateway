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
    private final String logsSquashSchedule;
    private final Duration batchSchedulerShutdownTimeout;
    private final Duration serverStopTimeout;
    private final String sslKeystorePassword;
    private final int serverPort;
    private final int socketQueueSize;
    private final String sslKeystoreName;

    ///
    public DevProperties() {

        currentEnvironment = Environment.DEV;
        logsSquashSchedule = "m1";
        batchSchedulerShutdownTimeout = Duration.ofSeconds(4);
        serverStopTimeout = Duration.ofSeconds(4);
        sslKeystorePassword = "password";
        serverPort = 8080;
        socketQueueSize = 0;
        sslKeystoreName = "keystore.p12";
    }

    ///
}
