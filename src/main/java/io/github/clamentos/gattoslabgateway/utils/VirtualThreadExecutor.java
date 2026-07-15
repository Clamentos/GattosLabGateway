package io.github.clamentos.gattoslabgateway.utils;

///
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicLong;

///
public final class VirtualThreadExecutor implements Executor {

    ///
    private final String prefix;
    private final AtomicLong index;

    ///
    public VirtualThreadExecutor(final String prefix) {

        this.prefix = prefix;
        index = new AtomicLong();
    }

    ///
    @Override
    public void execute(final Runnable command) {

        GenericUtils.spawnVirtualThread(prefix + index.getAndIncrement(), command);
    }

    ///
}
