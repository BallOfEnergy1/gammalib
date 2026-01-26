package com.gamma.gammalib.multi.nibblearray;

import java.util.concurrent.atomic.AtomicLongArray;

import com.google.common.annotations.VisibleForTesting;

public class AtomicNibbleArray8Safe implements FastAtomicNibbleArray {

    private final AtomicLongArray array;

    public AtomicNibbleArray8Safe(final int length) {
        this.array = new AtomicLongArray((length + 15) >>> 4);
    }

    public void set(final int i, final int newValue) {
        final int idx = i >>> 4;
        final int shift = (i & 15) << 2;
        final long mask = 0xFL << shift;
        final long valueToInject = (newValue & 0xFL) << shift;

        long oldLong, newLong;
        do {
            oldLong = this.array.get(idx);
            newLong = (oldLong & ~mask) | valueToInject;
        } while (!this.array.compareAndSet(idx, oldLong, newLong));
    }

    public int get(final int i) {
        return (int) ((this.array.get(i >>> 4) >> ((i & 15) << 2)) & 0xFL);
    }

    public boolean compareAndSet(int i, int expect, int newValue) {
        int idx = i >>> 4;
        int shift = (i & 15) << 2;
        long mask = 0xFL << shift;
        long expected = (expect & 0xFL) << shift;
        long valueToInject = (newValue & 0xFL) << shift;

        long num2, num;
        do {
            num = this.array.get(idx);
            if ((num & mask) != expected) return false;
            num2 = (num & ~mask) | valueToInject;
        } while (!this.array.compareAndSet(idx, num, num2));
        return true;
    }

    @VisibleForTesting
    public int incrementAndGet(final int i) {
        int old, newValue;
        do {
            old = get(i);
            newValue = (old + 1) & 0xF;
        } while (!compareAndSet(i, old, newValue));
        return newValue;
    }
}
