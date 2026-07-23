package io.github.clamentos.gattoslabgateway.configuration;

///
import io.github.clamentos.gattoslabgateway.observability.logging.entities.LogSeverity;

///..
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

///
@NoArgsConstructor(access = AccessLevel.PRIVATE)

///
public final class Constants {

    ///
    public static final char FIELD_SEPARATOR = '|';
    public static final char DYNAMIC_PROPERTY_ARRAY_SEPARATOR = ',';
    public static final char DYNAMIC_PROPERTY_RANGE_SEPARATOR = '-';
    public static final char DYNAMIC_PROPERTY_COMMENT = '#';
    public static final char NEWLINE_REPLACEMENT = '~';
    public static final String UNKNOWN_LOGGER = "UNKNOWN_LOGGER";

    public static final LogSeverity LOG_SEVERITY = LogSeverity.INFO;
    public static final long LOG_DRAIN_SIPHON_TASK_PERIOD = 60000L;
    public static final long LOG_WORKER_JOIN_TIMEOUT = 4000L;
    public static final int LOG_SIPHON_SIZE = 256;

    public static final int OBSERVABILITY_FILE_SEND_CHUNK_SIZE = 100;
    public static final long BATCH_SCHEDULER_SLEEP = 200L; // TODO: move to application properties

    ///
}
