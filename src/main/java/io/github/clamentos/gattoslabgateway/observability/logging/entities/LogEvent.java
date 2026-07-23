package io.github.clamentos.gattoslabgateway.observability.logging.entities;

///
import io.github.clamentos.gattoslabgateway.configuration.Constants;
import io.github.clamentos.gattoslabgateway.utils.GenericUtils;

///..
import lombok.Getter;
import lombok.Setter;

///
@Getter
@Setter

///
public final class LogEvent {

    ///
    private static final String SEPARATOR = Character.toString(Constants.FIELD_SEPARATOR);

    ///.
    private long id;
    private long timestamp;
    private LogSeverity severity;
    private String thread;
    private String logger;
    private String message;
    private Throwable exception;

    ///
    @Override
    public String toString() {

        final String value =

            id + SEPARATOR +
            timestamp + SEPARATOR +
            severity + SEPARATOR +
            thread + SEPARATOR +
            logger + SEPARATOR +
            GenericUtils.fastRemove(message, Constants.FIELD_SEPARATOR) + SEPARATOR +
            GenericUtils.fastRemove(exception.toString(), Constants.FIELD_SEPARATOR)
        ;

        return GenericUtils.fastReplace(value, '\n', Constants.NEWLINE_REPLACEMENT);
    }

    ///
}
