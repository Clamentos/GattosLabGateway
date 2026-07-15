package io.github.clamentos.gattoslabgateway.observability.metrics.contexts;

///
import io.github.clamentos.gattoslabgateway.eventbus.EventBus;
import io.github.clamentos.gattoslabgateway.observability.metrics.entities.RequestMetricsEntity;
import io.github.clamentos.gattoslabgateway.utils.FastAtomicCounter;
import io.github.clamentos.gattoslabgateway.utils.Siphon;

///..
import java.util.List;

///
public final class RequestMetricsContext {

    ///
    private final Siphon<RequestMetricsEntity> siphon;
    private final FastAtomicCounter visitorCounter;

    ///
    public RequestMetricsContext(final EventBus eventBus, final int siphonCapacity) {

        siphon = new Siphon<>(eventBus, siphonCapacity, RequestMetricsEntity::new);
        visitorCounter = new FastAtomicCounter();
    }

    ///
    public boolean updateMetrics(final String path, final String userAgent, final boolean isUnknown, final long startTime, final int httpStatus) {

        final RequestMetricsEntity entity = siphon.getNext();
        final long endTime = System.currentTimeMillis();

        if(entity != null) {

            visitorCounter.increment();

            entity.setPath(path);
            entity.setUserAgent(userAgent);
            entity.setUnknown(isUnknown);
            entity.setTimestamp(endTime);
            entity.setLatency((int)endTime - (int)startTime);
            entity.setHttpStatus((short)httpStatus);

            visitorCounter.decrement();
            return true;
        }

        return false;
    }

    ///.
    public List<RequestMetricsEntity> drainSiphon() {

        return siphon.drain();
    }

    ///..
    public boolean isNoOneThere() {

        return visitorCounter.get() == 0;
    }

    ///..
    public void reset() {

        siphon.reset();
    }

    ///
}
