package com.gamma.gammalib.multi.nibblearray;

import sun.misc.Unsafe;

public class AtomicNibbleArray8 implements FastAtomicNibbleArray {

    private static volatile boolean OFFSETS_READY;
    private static long LONG_BASE;
    private static int LONG_SHIFT;

    private final Unsafe unsafe;
    private final long[] array;

    public AtomicNibbleArray8(final int length) {
        Unsafe u = getUnsafe();
        if (u == null) {
            throw new IllegalStateException("Unsafe is unavailable");
        }
        this.unsafe = u;
        initOffsets(u);
        this.array = new long[(length + 15) >>> 4];
    }

    public void set(final int i, final int newValue) {
        final int idx = i >>> 4;
        final int shift = (i & 15) << 2;
        final long mask = 0xFL << shift;
        final long valueToInject = (newValue & 0xFL) << shift;

        final long offset = longOffset(idx);
        long oldLong;
        long newLong;
        do {
            oldLong = unsafe.getLongVolatile(array, offset);
            newLong = (oldLong & ~mask) | valueToInject;
        } while (!unsafe.compareAndSwapLong(array, offset, oldLong, newLong));
    }

    public int get(final int i) {
        long word = unsafe.getLongVolatile(array, longOffset(i >>> 4));
        return (int) ((word >>> ((i & 15) << 2)) & 0xFL);
    }

    public boolean compareAndSet(int i, int expect, int newValue) {
        int idx = i >>> 4;
        int shift = (i & 15) << 2;
        long mask = 0xFL << shift;
        long expected = (expect & 0xFL) << shift;
        long valueToInject = (newValue & 0xFL) << shift;

        final long offset = longOffset(idx);
        long num;
        long num2;
        do {
            num = unsafe.getLongVolatile(array, offset);
            if ((num & mask) != expected) return false;
            num2 = (num & ~mask) | valueToInject;
        } while (!unsafe.compareAndSwapLong(array, offset, num, num2));
        return true;
    }

    public int incrementAndGet(final int i) {
        int old;
        int newValue;
        do {
            old = get(i);
            newValue = (old + 1) & 0xF;
        } while (!compareAndSet(i, old, newValue));
        return newValue;
    }

    private static void initOffsets(Unsafe unsafe) {
        if (OFFSETS_READY) return;
        synchronized (AtomicNibbleArray8.class) {
            if (OFFSETS_READY) return;
            LONG_BASE = unsafe.arrayBaseOffset(long[].class);
            int scale = unsafe.arrayIndexScale(long[].class);
            if (scale != Long.BYTES) {
                throw new ExceptionInInitializerError("Unexpected long[] scale: " + scale);
            }
            LONG_SHIFT = 3;
            OFFSETS_READY = true;
        }
    }

    private static long longOffset(int index) {
        return LONG_BASE + ((long) index << LONG_SHIFT);
    }
}
