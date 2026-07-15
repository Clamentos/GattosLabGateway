package io.github.clamentos.gattoslabgateway.utils;

///
import java.util.concurrent.atomic.AtomicLongArray;

///
public final class FastAtomicCounter {

    ///
    private static final int CPU_CACHE_LINE_SIZE_BYTES = 64;

    ///..
    private final int mask;
    private final AtomicLongArray paddedCounters;

    ///
    public FastAtomicCounter() {

        final int elements = Runtime.getRuntime().availableProcessors() * CPU_CACHE_LINE_SIZE_BYTES;

        mask = elements - 1;
        paddedCounters = new AtomicLongArray(elements);
    }

    ///
    public void increment() {

        paddedCounters.incrementAndGet(this.getIndex());
    }

    ///..
    public void decrement() {

        paddedCounters.decrementAndGet(this.getIndex());
    }

    ///..
    public long get() {

        long total = 0;
        for(int i = 0; i < paddedCounters.length(); i += 8) total += paddedCounters.get(i);

        return total;
    }

    ///.
    private int getIndex() {

        return (int)((Thread.currentThread().threadId() << 3) & mask);
    }

    ///
}
