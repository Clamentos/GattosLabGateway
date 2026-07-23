package io.github.clamentos.gattoslabgateway.configuration;

///
import io.github.clamentos.gattoslabgateway.configuration.environments.Environment;

///..
import java.time.Duration;

///
public abstract class ApplicationProperties {

    ///
    private static final String PREFIX = "ApplicationProperties.resolve :: ";

    ///
    public abstract Environment getCurrentEnvironment();

    ///..
    public abstract int getServerPort();
    public abstract int getSocketQueueSize();
    public abstract String getSslKeystoreName();
    public abstract String getSslKeystorePassword();
    public abstract Duration getServerStopTimeout();

    ///..
    public abstract Duration getBatchSchedulerShutdownTimeout();

    ///..
    public abstract String getDynamicPropertiesRefreshSchedule();
    public abstract String getDynamicPropertiesPath();

    ///..
    public abstract String getRequestMetricsFilePath();
    public abstract int getRequestMetricsBufferSize();

    ///..
    public abstract String getSystemMetricsFilePath();
    public abstract String getSystemMetricsPollSchedule();

    ///..
    public abstract String getLogsSquashSchedule();

    ///..
    public abstract String getObservabilitySendSchedule();

    ///
    protected <T> T resolve(final String envName, final Class<T> clazz) throws IllegalArgumentException {

        final String envValue = System.getenv(envName);
        if(envValue == null) throw new IllegalArgumentException(PREFIX + "The environment variable '" + envName + "' is not defined");

        if(clazz == String.class) return clazz.cast(envValue);
        if(clazz == Integer.class) return clazz.cast(Integer.parseInt(envValue));
        if(clazz == Long.class) return clazz.cast(Long.parseLong(envValue));
        if(clazz == Boolean.class) return clazz.cast(Boolean.parseBoolean(envValue));

        throw new IllegalArgumentException(PREFIX + "Unknown variable type '" + clazz.getSimpleName() + "'");
    }

    ///
}
