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
public final class ProdProperties extends ApplicationProperties {

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
    public ProdProperties() {

        currentEnvironment = Environment.PROD;
        logsSquashSchedule = "m1";
        batchSchedulerShutdownTimeout = Duration.ofSeconds(4);
        serverStopTimeout = Duration.ofSeconds(4);
        sslKeystorePassword = super.resolve("SSL_KEY_STORE_PASSWORD", String.class);
        serverPort = 8443;
        socketQueueSize = 0;
        sslKeystoreName = "keystore.p12";
    }

    ///
}
