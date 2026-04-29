package com.gamma.gammalib.multi.nibblearray;

import com.gamma.gammalib.unsafe.UnsafeAccessor;
import com.google.common.annotations.VisibleForTesting;

import sun.misc.Unsafe;

public class WrappedAtomicNibbleArray8 implements FastAtomicNibbleArray {

    private static volatile boolean OFFSETS_READY;
    private static long BYTE_BASE;
    private static int BYTE_SHIFT;

    private final Unsafe unsafe;
    private final byte[] array;

    public WrappedAtomicNibbleArray8(final int length) {
        Unsafe u = UnsafeAccessor.getUnsafe();
        if (u == null) {
            throw new IllegalStateException("Unsafe is unavailable");
        }
        this.unsafe = u;
        initOffsets(u);
        this.array = new byte[(length + 1) >>> 1];
    }

    public WrappedAtomicNibbleArray8(final byte[] array) {
        Unsafe u = UnsafeAccessor.getUnsafe();
        if (u == null) {
            throw new IllegalStateException("Unsafe is unavailable");
        }
        this.unsafe = u;
        initOffsets(u);
        this.array = array;
    }

    @Override
    public void set(final int i, final int newValue) {
        final byte[] array = this.array;
        final int byteIdx = i >>> 1;
        final int longIdx = byteIdx & ~7;
        final int shift = ((byteIdx & 7) << 3) + ((i & 1) << 2);
        final long mask = 0xFL << shift;
        final long valueToInject = (long) (newValue & 0xF) << shift;

        final long offset = byteOffset(longIdx);
        long oldLong, newLong;
        do {
            oldLong = unsafe.getLongVolatile(array, offset);
            newLong = (oldLong & ~mask) | valueToInject;
        } while (!unsafe.compareAndSwapLong(array, offset, oldLong, newLong));
    }

    @Override
    public int get(final int i) {
        final byte[] array = this.array;
        final int byteIdx = i >>> 1;
        final int shift = (i & 1) << 2;
        byte b = unsafe.getByteVolatile(array, byteOffset(byteIdx));
        return (b >>> shift) & 0xF;
    }

    @Override
    public boolean compareAndSet(int i, int expect, int newValue) {
        final byte[] array = this.array;
        final int byteIdx = i >>> 1;
        final int longIdx = byteIdx & ~7;
        final int shift = ((byteIdx & 7) << 3) + ((i & 1) << 2);
        final long mask = 0xFL << shift;
        final long expected = (long) (expect & 0xF) << shift;
        final long valueToInject = (long) (newValue & 0xF) << shift;

        final long offset = byteOffset(longIdx);
        long oldLong, newLong;
        do {
            oldLong = unsafe.getLongVolatile(array, offset);
            if ((oldLong & mask) != expected) return false;
            newLong = (oldLong & ~mask) | valueToInject;
        } while (!unsafe.compareAndSwapLong(array, offset, oldLong, newLong));
        return true;
    }

    @Override
    @VisibleForTesting
    public int incrementAndGet(final int i) {
        final byte[] array = this.array;
        final int byteIdx = i >>> 1;
        final int longIdx = byteIdx & ~7;
        final int shift = ((byteIdx & 7) << 3) + ((i & 1) << 2);
        final long mask = 0xFL << shift;

        final long offset = byteOffset(longIdx);
        long oldLong, newLong;
        int newNibble;
        do {
            oldLong = unsafe.getLongVolatile(array, offset);
            int oldNibble = (int) ((oldLong >>> shift) & 0xF);
            newNibble = (oldNibble + 1) & 0xF;
            newLong = (oldLong & ~mask) | ((long) newNibble << shift);
        } while (!unsafe.compareAndSwapLong(array, offset, oldLong, newLong));
        return newNibble;
    }

    @Override
    public byte[] getByteArray() {
        return array;
    }

    private static void initOffsets(Unsafe unsafe) {
        if (OFFSETS_READY) return;
        synchronized (WrappedAtomicNibbleArray8.class) {
            if (OFFSETS_READY) return;
            BYTE_BASE = unsafe.arrayBaseOffset(byte[].class);
            int scale = unsafe.arrayIndexScale(byte[].class);
            if (scale != 1) {
                throw new ExceptionInInitializerError("Unexpected byte[] scale: " + scale);
            }
            BYTE_SHIFT = 0;
            OFFSETS_READY = true;
        }
    }

    private static long byteOffset(int index) {
        return BYTE_BASE + ((long) index << BYTE_SHIFT);
    }
}
