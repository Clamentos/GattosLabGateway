package io.github.clamentos.gattoslabgateway.eventbus;

///
import io.github.clamentos.gattoslabgateway.utils.GenericUtils;

///..
import java.util.concurrent.atomic.AtomicLong;

///
public final class EventBus {

    ///
    private final AtomicLong workerIndex;
    private final Runnable command;

    ///
    public EventBus(final Runnable command) {

        workerIndex = new AtomicLong();
        this.command = command;
    }

    ///
    public void trigger() {

        GenericUtils.spawnVirtualThread("gattos-lab-gateway-event-bus-worker-" + workerIndex.getAndIncrement(), command);
    }

    ///
}
