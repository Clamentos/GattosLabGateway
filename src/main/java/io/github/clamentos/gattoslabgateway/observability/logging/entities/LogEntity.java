package io.github.clamentos.gattoslabgateway.observability.logging.entities;

///
import io.github.clamentos.gattoslabgateway.utils.GenericUtils;

///..
import java.util.ArrayList;
import java.util.List;

///..
import lombok.AllArgsConstructor;
import lombok.Getter;

///
@AllArgsConstructor
@Getter

///
public final class LogEntity {

    ///
    public static final char NULL_REPLACEMENT = '\u0000';
    public static final char SECTION_SEPARATOR = '\u0001';
    public static final char MESSAGE_LINE_SEPARATOR = '\u0002';

    ///
    private final long timestamp;
    private final String severity;
    private final String thread;
    private final String logger;
    private final String message;
    private final LogEntityExceptionEntry exception;

    ///
    public LogEntity(final String log) {

        final List<String> splits = GenericUtils.fastSplit(log, SECTION_SEPARATOR);

        timestamp = Long.parseLong(splits.get(0));
        severity = this.undoNormalization(splits.get(1));
        thread = this.undoNormalization(splits.get(2));
        logger = this.undoNormalization(splits.get(3));
        message = this.undoNormalization(GenericUtils.fastReplace(splits.get(4), MESSAGE_LINE_SEPARATOR, '\n'));

        if(splits.size() > 5) {

            final String excClassName = splits.get(5);
            final String excMessage = this.undoNormalization(GenericUtils.fastReplace(splits.get(6), MESSAGE_LINE_SEPARATOR, '\n'));

            List<String> excStacktrace = null;

            if(splits.size() > 7) {

                excStacktrace = new ArrayList<>(splits.size() - 7);
                for(int i = 7; i < splits.size(); i++) excStacktrace.add(this.undoNormalization(GenericUtils.fastReplace(splits.get(i), MESSAGE_LINE_SEPARATOR, '\n')));
            }

            exception = new LogEntityExceptionEntry(excClassName, excMessage, excStacktrace);
        }

        else {

            exception = null;
        }
    }

    ///
    private String undoNormalization(final String input) {

        if(input != null && !input.isEmpty() && NULL_REPLACEMENT == input.charAt(0)) return null;
        else return input;
    }

    ///
}
