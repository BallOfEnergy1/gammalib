package com.gamma.gammalib.multi.state;

import java.util.function.Consumer;
import java.util.function.Function;

import com.gamma.gammalib.util.concurrent.IAtomic;

public interface FastThreadLocalState<T> extends IAtomic {

    void runWithValue(Runnable runnable);

    void runWithValue(Consumer<T> consumer);

    <R> R runWithValue(Function<T, R> function);

    T getCurrentValue();
}
