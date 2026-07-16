package io.github.clamentos.gattoslabgateway.observability.logging.squash;

///
public interface SquashLogEvent {

    ///
    public SquashLogEventType getType();

    ///..
    public void update(final Object value);
    public void log();
    public void reset();

    ///
}
