package com.gamma.gammalib.asm.util;

import java.util.function.Supplier;

import com.github.bsideup.jabel.Desugar;

@Desugar
public record ParallelSupplier<T> (Supplier<T> supplier) {

    public T get() {
        synchronized (supplier) {
            return supplier.get();
        }
    }
}
