package io.github.clamentos.gattoslabgateway.observability.logging.squashing;

///
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

///
public class RateLimitSquasher implements LogSquasher {

    ///
    protected final Map<String, AtomicInteger> squashesMap;

    ///
    public RateLimitSquasher() {

        squashesMap = new ConcurrentHashMap<>();
    }

    ///
    @Override
    public SquashLogEventType getEventType() {

        return SquashLogEventType.RATE_LIMIT;
    }

    ///..
    @Override
    public void update(final String value) {

        if(value != null) squashesMap.computeIfAbsent(value, _ -> new AtomicInteger()).incrementAndGet();
    }

    ///..
    @Override
    public List<String> composeLogs() {

        final List<String> logs = new ArrayList<>(squashesMap.size());

        for(final Map.Entry<String, AtomicInteger> rateLimitSquashEntry : squashesMap.entrySet()) {

            logs.add("Rate limit reached " + rateLimitSquashEntry.getValue() + " times for fingerprint '" + rateLimitSquashEntry.getKey() + "'");
        }

        return logs;
    }

    ///..
    @Override
    public void reset() {

        squashesMap.clear();
    }

    ///
}
