package io.github.clamentos.gattoslabgateway.observability.metrics.contexts;

///
import io.github.clamentos.gattoslabgateway.datastructures.FastAtomicCounter;
import io.github.clamentos.gattoslabgateway.datastructures.Siphon;

///..
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

///
public final class SiphonContext<T> {

    ///
    private final Siphon<T> siphon;
    private final FastAtomicCounter visitorCounter;

    ///
    public SiphonContext(final Consumer<List<T>> drainTask, final int siphonCapacity, final Supplier<T> supplier) {

        siphon = new Siphon<>(drainTask, siphonCapacity, supplier);
        visitorCounter = new FastAtomicCounter();
    }

    ///
    public boolean update(final Consumer<T> consumer) {

        final T entity = siphon.getNext();

        if(entity != null) {

            visitorCounter.increment();
            consumer.accept(entity);
            visitorCounter.decrement();

            return true;
        }

        return false;
    }

    ///.
    public List<T> drainSiphon() {

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
