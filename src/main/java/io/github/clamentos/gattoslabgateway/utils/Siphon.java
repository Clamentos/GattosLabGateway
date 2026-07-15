package io.github.clamentos.gattoslabgateway.utils;

///
import io.github.clamentos.gattoslabgateway.eventbus.EventBus;

///..
import java.util.AbstractList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReferenceArray;
import java.util.function.Supplier;

///
public final class Siphon<T> extends AbstractList<T> {

    ///
    private final EventBus eventBus;
    private final AtomicBoolean isDraining;

    ///..
    private final AtomicReferenceArray<T> elements;
    private final AtomicInteger index;

    ///
    public Siphon(final EventBus eventBus, final int capacity, final Supplier<T> supplier) {

        this.eventBus = eventBus;
        isDraining = new AtomicBoolean();

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
        if(isDraining.compareAndSet(false, true)) eventBus.trigger();

        return null;
    }

    ///..
    public List<T> drain() {

        isDraining.set(true);
        return this;
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
