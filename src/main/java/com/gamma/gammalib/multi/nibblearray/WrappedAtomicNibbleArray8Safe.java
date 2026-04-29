package com.gamma.gammalib.multi.nibblearray;

import com.google.common.annotations.VisibleForTesting;

public class WrappedAtomicNibbleArray8Safe implements FastAtomicNibbleArray {

    private final byte[] array;
    private final Object[] locks = new Object[16];

    public WrappedAtomicNibbleArray8Safe(final int length) {
        this.array = new byte[(length + 1) >>> 1];
        for (int i = 0; i < 16; i++) locks[i] = new Object();
    }

    public WrappedAtomicNibbleArray8Safe(final byte[] array) {
        this.array = array;
        for (int i = 0; i < 16; i++) locks[i] = new Object();
    }

    private Object getLock(int i) {
        return locks[i & 0xF];
    }

    @Override
    public void set(final int i, final int newValue) {
        final int byteIdx = i >>> 1;
        final int shift = (i & 1) << 2;
        final int mask = 0xF << shift;
        final int valueToInject = (newValue & 0xF) << shift;

        synchronized (getLock(byteIdx)) {
            int oldByte = array[byteIdx] & 0xFF;
            array[byteIdx] = (byte) ((oldByte & ~mask) | valueToInject);
        }
    }

    @Override
    public int get(final int i) {
        final int byteIdx = i >>> 1;
        final int shift = (i & 1) << 2;
        // Optimization: Reading a byte from a byte array is atomic.
        // We use the lock for visibility/consistency only.
        // For performance, we can skip the lock if we don't strictly need sequentially consistent reads,
        // but to maintain full atomicity as requested, we keep it or use a more efficient way.
        synchronized (getLock(byteIdx)) {
            return (array[byteIdx] >>> shift) & 0xF;
        }
    }

    @Override
    public boolean compareAndSet(int i, int expect, int newValue) {
        final int byteIdx = i >>> 1;
        final int shift = (i & 1) << 2;
        final int mask = 0xF << shift;
        final int expected = (expect & 0xF) << shift;
        final int valueToInject = (newValue & 0xF) << shift;

        synchronized (getLock(byteIdx)) {
            int oldByte = array[byteIdx] & 0xFF;
            if ((oldByte & mask) != expected) return false;
            array[byteIdx] = (byte) ((oldByte & ~mask) | valueToInject);
            return true;
        }
    }

    @Override
    @VisibleForTesting
    public int incrementAndGet(final int i) {
        final int byteIdx = i >>> 1;
        final int shift = (i & 1) << 2;
        final int mask = 0xF << shift;

        synchronized (getLock(byteIdx)) {
            int oldByte = array[byteIdx] & 0xFF;
            int oldNibble = (oldByte >>> shift) & 0xF;
            int newNibble = (oldNibble + 1) & 0xF;
            array[byteIdx] = (byte) ((oldByte & ~mask) | (newNibble << shift));
            return newNibble;
        }
    }

    @Override
    public byte[] getByteArray() {
        return array;
    }
}
