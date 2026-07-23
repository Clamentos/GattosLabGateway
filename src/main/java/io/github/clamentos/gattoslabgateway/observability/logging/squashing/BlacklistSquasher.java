package io.github.clamentos.gattoslabgateway.observability.logging.squashing;

///
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

///
public final class BlacklistSquasher extends RateLimitSquasher {

    ///
    @Override
    public SquashLogEventType getEventType() {

        return SquashLogEventType.BLACKLISTED;
    }

    ///..
    @Override
    public List<String> composeLogs() {

        final List<String> logs = new ArrayList<>(super.squashesMap.size());

        for(final Map.Entry<String, AtomicInteger> squashEntry : super.squashesMap.entrySet()) {

            logs.add("Blocked " + squashEntry.getValue() + " times by blacklist for fingerprint '" + squashEntry.getKey() + "'");
        }

        return logs;
    }

    ///
}
