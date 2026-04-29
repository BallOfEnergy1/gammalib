package com.gamma.gammalib.multi.state;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class ThreadLocalState25<T> implements FastThreadLocalState<T> {

    private final ScopedValue<T> scopedValue = ScopedValue.newInstance();
    private final Supplier<T> supplier;

    public ThreadLocalState25(Supplier<T> supplier) {
        this.supplier = supplier;
    }

    @Override
    public void runWithValue(Runnable runnable) {
        ScopedValue.where(scopedValue, supplier.get())
            .run(runnable);
    }

    @Override
    public void runWithValue(Consumer<T> consumer) {
        ScopedValue.where(scopedValue, supplier.get())
            .run(() -> consumer.accept(scopedValue.get()));
    }

    @Override
    public <R> R runWithValue(Function<T, R> function) {
        return ScopedValue.where(scopedValue, supplier.get())
            .call(() -> function.apply(scopedValue.get()));
    }

    @Override
    public T getCurrentValue() {
        return scopedValue.get();
    }
}
