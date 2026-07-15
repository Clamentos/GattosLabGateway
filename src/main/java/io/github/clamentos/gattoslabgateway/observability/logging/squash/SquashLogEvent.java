package io.github.clamentos.gattoslabgateway.observability.logging.squash;

///
public interface SquashLogEvent {

    ///
    SquashLogEventType getType();

    ///..
    void update(final Object value);
    void log();
    void reset();

    ///
}
