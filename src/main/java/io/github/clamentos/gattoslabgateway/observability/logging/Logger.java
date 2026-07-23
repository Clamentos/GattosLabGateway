package io.github.clamentos.gattoslabgateway.observability.logging;

///
import io.github.clamentos.gattoslabgateway.configuration.Constants;

///
public final class Logger {

    ///
    private final String name;

    ///..
    private final LoggerRoot loggerRoot;

    ///
    public Logger() {

        final Class<?> caller = StackWalker.getInstance(StackWalker.Option.RETAIN_CLASS_REFERENCE)

            .walk(frames -> frames
            .skip(1)
            .findFirst()
            .map(StackWalker.StackFrame::getDeclaringClass)
            .orElse(null))
        ;

        this(caller != null ? caller.getSimpleName() : Constants.UNKNOWN_LOGGER);
    }

    ///..
    public Logger(final String name) {

        this.name = name;
        loggerRoot = LoggerRoot.getInstance();
    }

    ///
    public void info(final String message) {

        loggerRoot.info(name, message);
    }

    ///..
    public void warn(final String message) {

        loggerRoot.warn(name, message);
    }

    ///..
    public void error(final String message) {

        loggerRoot.error(name, message, null);
    }

    ///..
    public void error(final String message, final Throwable exception) {

        loggerRoot.error(name, message, exception);
    }

    ///
}
