package io.github.clamentos.gattoslabgateway.observability.logging.squashing;

///
import java.util.List;

///
public interface LogSquasher {

    ///
    public SquashLogEventType getEventType();

    ///..
    public void update(final String value);
    public List<String> composeLogs();
    public void reset();

    ///
}
