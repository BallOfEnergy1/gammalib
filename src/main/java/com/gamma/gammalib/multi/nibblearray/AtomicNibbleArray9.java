package com.gamma.gammalib.multi.nibblearray;

import java.lang.invoke.MethodHandles;
import java.lang.invoke.VarHandle;

import com.google.common.annotations.VisibleForTesting;

public class AtomicNibbleArray9 implements FastAtomicNibbleArray {

    private static final VarHandle LONG_ARRAY_HANDLE = MethodHandles.arrayElementVarHandle(long[].class);

    private final long[] array;

    public AtomicNibbleArray9(final int length) {
        this.array = new long[(length + 15) >>> 4];
    }

    public void set(final int i, final int newValue) {
        final long[] array = this.array;
        final int idx = i >>> 4;
        final int shift = (i & 15) << 2;
        final long mask = 0xFL << shift;
        final long valueToInject = (newValue & 0xFL) << shift;

        long oldLong, newLong;
        for (;;) {
            oldLong = (long) LONG_ARRAY_HANDLE.getOpaque(array, idx);
            newLong = (oldLong & ~mask) | valueToInject;
            if (LONG_ARRAY_HANDLE.weakCompareAndSetPlain(array, idx, oldLong, newLong)) break;
            Thread.onSpinWait();
        }
    }

    public int get(final int i) {
        final long[] array = this.array;
        final int idx = i >>> 4;
        final int shift = (i & 15) << 2;
        return (int) (((long) LONG_ARRAY_HANDLE.getOpaque(array, idx) >> shift) & 0xFL);
    }

    public boolean compareAndSet(int i, int expect, int newValue) {
        final long[] array = this.array;
        int idx = i >>> 4;
        int shift = (i & 15) << 2;
        long mask = 0xFL << shift;
        long expected = (expect & 0xFL) << shift;
        long valueToInject = (newValue & 0xFL) << shift;

        long num2, num;
        for (;;) {
            num = (long) LONG_ARRAY_HANDLE.getOpaque(array, idx);

            if ((num & mask) != expected) return false;

            num2 = (num & ~mask) | valueToInject;
            if (LONG_ARRAY_HANDLE.weakCompareAndSetPlain(array, idx, num, num2)) break;
            Thread.onSpinWait();
        }
        return true;
    }

    @VisibleForTesting
    public int incrementAndGet(final int i) {
        int old, newValue;
        for (;;) {
            old = get(i);
            newValue = (old + 1) & 0xF;
            if (compareAndSet(i, old, newValue)) break;
            Thread.onSpinWait();
        }
        return newValue;
    }
}
