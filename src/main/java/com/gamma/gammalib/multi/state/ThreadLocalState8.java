package com.gamma.gammalib.multi.state;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class ThreadLocalState8<T> implements FastThreadLocalState<T> {

    private final ThreadLocal<T> threadLocal;
    private final boolean intentionalLeak;

    public ThreadLocalState8(Supplier<T> supplier, boolean intentionalLeak) {
        threadLocal = ThreadLocal.withInitial(supplier);
        this.intentionalLeak = intentionalLeak;
    }

    @Override
    public void runWithValue(Runnable runnable) {
        try {
            runnable.run();
        } finally {
            if (!intentionalLeak) threadLocal.remove();
        }
    }

    @Override
    public void runWithValue(Consumer<T> consumer) {
        try {
            consumer.accept(threadLocal.get());
        } finally {
            if (!intentionalLeak) threadLocal.remove();
        }
    }

    @Override
    public <R> R runWithValue(Function<T, R> function) {
        try {
            return function.apply(threadLocal.get());
        } finally {
            if (!intentionalLeak) threadLocal.remove();
        }
    }

    @Override
    public T getCurrentValue() {
        return threadLocal.get();
    }
}
