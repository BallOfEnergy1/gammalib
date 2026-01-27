package com.gamma.gammalib.bench.misc;

import java.util.BitSet;

import com.gamma.gammalib.multi.bitset.FastAtomicBitSet;

public class JavaBitSetProxy extends BitSet implements FastAtomicBitSet {

    public JavaBitSetProxy(int length) {
        super(length);
    }

    @Override
    public synchronized boolean get(int bitIndex) {
        return super.get(bitIndex);
    }

    @Override
    public synchronized void set(int bitIndex, boolean value) {
        super.set(bitIndex, value);
    }

    @Override
    public synchronized void set(int bitIndex) {
        super.set(bitIndex);
    }

    @Override
    public synchronized void clear(int bitIndex) {
        super.clear(bitIndex);
    }

    @Override
    public synchronized void flip(int bitIndex) {
        super.flip(bitIndex);
    }

    @Override
    public synchronized int length() {
        return super.length();
    }
}
