package io.github.clamentos.gattoslabgateway.configuration;

///
import io.github.clamentos.gattoslabgateway.configuration.environments.Environment;

///..
import java.time.Duration;

///
public abstract class ApplicationProperties {

    ///
    private static final String SOURCE_RESOLVE = "ApplicationProperties.resolve";

    ///
    public abstract Environment getCurrentEnvironment();

    ///..
    public abstract String getLogsSquashSchedule();
    public abstract Duration getBatchSchedulerShutdownTimeout();
    public abstract Duration getServerStopTimeout();
    public abstract String getSslKeystorePassword();
    public abstract int getServerPort();
    public abstract int getSocketQueueSize();
    public abstract String getSslKeystoreName();
    public abstract String getDynamicPropertiesRefreshSchedule();

    ///
    protected <T> T resolve(final String envName, final Class<T> clazz) throws IllegalArgumentException {

        final String envValue = System.getenv(envName);
        if(envValue == null) throw new IllegalArgumentException(SOURCE_RESOLVE + ":: The environment variable '" + envName + "' is not defined");

        if(clazz == String.class) return clazz.cast(envValue);
        if(clazz == Integer.class) return clazz.cast(Integer.parseInt(envValue));
        if(clazz == Long.class) return clazz.cast(Long.parseLong(envValue));
        if(clazz == Boolean.class) return clazz.cast(Boolean.parseBoolean(envValue));

        throw new IllegalArgumentException(SOURCE_RESOLVE + ":: Unknown variable type '" + clazz.getSimpleName() + "'");
    }

    ///
}
