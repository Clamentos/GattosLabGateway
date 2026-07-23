package io.github.clamentos.gattoslabgateway.datastructures;

///
import io.github.clamentos.gattoslabgateway.utils.VirtualThreadExecutor;

///..
import java.util.AbstractList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReferenceArray;
import java.util.function.Consumer;
import java.util.function.Supplier;

///
public final class Siphon<T> extends AbstractList<T> {

    ///
    private final AtomicBoolean isDraining;
    private final Consumer<List<T>> drainTask;
    private final VirtualThreadExecutor taskLauncher;

    ///..
    private final AtomicReferenceArray<T> elements;
    private final AtomicInteger index;

    ///
    public Siphon(final Consumer<List<T>> drainTask, final int capacity, final Supplier<T> supplier) {

        isDraining = new AtomicBoolean();
        this.drainTask = drainTask;
        taskLauncher = new VirtualThreadExecutor("siphon-drain-task-");

        @SuppressWarnings("unchecked")
        final T[] entities = (T[])new Object[capacity];

        for(int i = 0; i < capacity; i++) entities[i] = supplier.get();
        this.elements = new AtomicReferenceArray<>(entities);

        index = new AtomicInteger();
    }

    ///
    public T getNext() {

        final int indexValue = index.getAndUpdate(val -> val < elements.length() ? val + 1 : val);

        if(indexValue < elements.length()) return elements.get(indexValue);
        if(isDraining.compareAndSet(false, true)) taskLauncher.execute(() -> drainTask.accept(this));

        return null;
    }

    ///..
    public List<T> drain() {

        if(isDraining.compareAndSet(false, true)) return this;
        return List.of();
    }

    ///..
    public void reset() {

        index.set(0);
        isDraining.set(false);
    }

    ///..
    @Override
    public int size() {

        return index.get();
    }

    ///..
    @Override
    public T get(final int index) {

        return elements.get(index);
    }

    ///..
    @Override
    public boolean equals(final Object other) {

        if(other == this) return true;
        if(!(other instanceof Siphon)) return false;

        final Siphon<?> otherCasted = (Siphon<?>)other;
        if(otherCasted.size() != this.size()) return false;

        for(int i = 0; i < this.size(); i++) {

            final T thisElem = this.get(i);
            final Object otherElem = otherCasted.get(i);

            if(thisElem != null) {

                if(!thisElem.equals(otherElem)) return false;
            }

            else if(otherElem != null) return false;
        }

        return true;
    }

    ///..
    @Override
    public int hashCode() {

        int result = 1;
        for(int i = 0; i < elements.length(); i++) result = (result * 31) + Objects.hashCode(elements.get(i));

        return result;
    }

    ///
}
