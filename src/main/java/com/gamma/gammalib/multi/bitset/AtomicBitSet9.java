package com.gamma.gammalib.multi.bitset;

import java.lang.invoke.MethodHandles;
import java.lang.invoke.VarHandle;

public class AtomicBitSet9 implements FastAtomicBitSet {

    private static final VarHandle ARRAY_HANDLE = MethodHandles.arrayElementVarHandle(long[].class);

    private final long[] values;

    private final int length;

    public AtomicBitSet9(int bitsLength) {
        values = new long[(bitsLength + 63) >>> 6];
        length = bitsLength;
    }

    @Override
    public boolean get(int bitIndex) {
        long[] values = this.values;
        long bit = 1L << (bitIndex & 63);
        return (((long) ARRAY_HANDLE.getOpaque(values, bitIndex >>> 6)) & bit) != 0L;
    }

    @Override
    public void set(int bitIndex, boolean v) {
        if (v) set(bitIndex);
        else clear(bitIndex);
    }

    @Override
    public void set(int bitIndex) {
        long[] values = this.values;
        int wordIndex = bitIndex >>> 6;
        long bit = 1L << (bitIndex & 63);
        long prev;
        long next;
        for (;;) {
            prev = (long) ARRAY_HANDLE.getOpaque(values, wordIndex);
            next = prev | bit;
            if (ARRAY_HANDLE.compareAndSet(values, wordIndex, prev, next)) break;
        }
    }

    @Override
    public void clear(int bitIndex) {
        long[] values = this.values;
        int wordIndex = bitIndex >>> 6;
        long bit = 1L << (bitIndex & 63);
        long prev;
        long next;
        for (;;) {
            prev = (long) ARRAY_HANDLE.getOpaque(values, wordIndex);
            next = prev & ~bit;
            if (ARRAY_HANDLE.compareAndSet(values, wordIndex, prev, next)) break;
        }
    }

    @Override
    public void flip(int bitIndex) {
        long[] values = this.values;
        int wordIndex = bitIndex >>> 6;
        long bit = 1L << (bitIndex & 63);
        long prev;
        long next;
        for (;;) {
            prev = (long) ARRAY_HANDLE.getOpaque(values, wordIndex);
            next = prev ^ bit;
            if (ARRAY_HANDLE.compareAndSet(values, wordIndex, prev, next)) break;
        }
    }

    @Override
    public int length() {
        return length;
    }
}
