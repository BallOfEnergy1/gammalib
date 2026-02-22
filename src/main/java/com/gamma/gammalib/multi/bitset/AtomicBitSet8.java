package com.gamma.gammalib.multi.bitset;

import java.util.concurrent.atomic.AtomicLongArray;

public class AtomicBitSet8 implements FastAtomicBitSet {

    private final AtomicLongArray values;

    private final int length;

    public AtomicBitSet8(int bitsLength) {
        values = new AtomicLongArray((bitsLength + 63) >>> 6);
        length = bitsLength;
    }

    @Override
    public boolean get(int bitIndex) {
        int wordIndex = bitIndex >>> 6;
        long bit = 1L << (bitIndex & 63);
        return (values.get(wordIndex) & bit) != 0;
    }

    @Override
    public void set(int bitIndex, boolean value) {
        if (value) set(bitIndex);
        else clear(bitIndex);
    }

    @Override
    public void set(int bitIndex) {
        int wordIndex = bitIndex >>> 6;
        long bit = 1L << (bitIndex & 63);
        long prev;
        do {
            prev = values.get(wordIndex);
            if ((prev & bit) != 0) return;
        } while (!values.compareAndSet(wordIndex, prev, prev | bit));
    }

    @Override
    public void clear(int bitIndex) {
        int wordIndex = bitIndex >>> 6;
        long bit = 1L << (bitIndex & 63);
        long prev;
        do {
            prev = values.get(wordIndex);
            if ((prev & bit) == 0) return;
        } while (!values.compareAndSet(wordIndex, prev, prev & ~bit));
    }

    @Override
    public void flip(int bitIndex) {
        int wordIndex = bitIndex >>> 6;
        long bit = 1L << (bitIndex & 63);
        long prev;
        do {
            prev = values.get(wordIndex);
        } while (!values.compareAndSet(wordIndex, prev, prev ^ bit));
    }

    @Override
    public int length() {
        return length;
    }
}
