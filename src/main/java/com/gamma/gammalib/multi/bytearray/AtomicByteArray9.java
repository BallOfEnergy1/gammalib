package com.gamma.gammalib.multi.bytearray;

import java.lang.invoke.MethodHandles;
import java.lang.invoke.VarHandle;
import java.nio.ByteOrder;

import com.google.common.annotations.VisibleForTesting;

public class AtomicByteArray9 implements FastAtomicByteArray {

    private static final int BYTES_PER_LONG = 8;
    private static final VarHandle LONG_ARRAY_HANDLE = MethodHandles.arrayElementVarHandle(long[].class);
    private static final VarHandle BYTE_LONG_VIEW = MethodHandles
        .byteArrayViewVarHandle(long[].class, ByteOrder.LITTLE_ENDIAN);
    private static final VarHandle CACHED_ARRAY_HANDLE;

    static {
        try {
            CACHED_ARRAY_HANDLE = MethodHandles.lookup()
                .findVarHandle(AtomicByteArray9.class, "cachedArray", byte[].class);
        } catch (ReflectiveOperationException e) {
            throw new ExceptionInInitializerError(e);
        }
    }

    private final long[] array;
    private final int length;
    private volatile byte[] cachedArray;

    public AtomicByteArray9(final int length) {
        this.length = length;
        this.array = new long[(length + 7) / BYTES_PER_LONG];
    }

    public void set(final int i, final byte newValue) {
        final long[] array = this.array;
        final int idx = i >>> 3;
        final int shift = (i & 7) << 3;
        final long mask = 0xFFL << shift;
        final long valueToInject = (newValue & 0xFFL) << shift;

        long oldLong, newLong;
        for (;;) {
            oldLong = (long) LONG_ARRAY_HANDLE.getOpaque(array, idx);
            newLong = (oldLong & ~mask) | valueToInject;
            if (LONG_ARRAY_HANDLE.weakCompareAndSetPlain(array, idx, oldLong, newLong)) break;
            Thread.onSpinWait();
        }
        CACHED_ARRAY_HANDLE.setOpaque(this, null);
    }

    public byte get(final int i) {
        final long[] array = this.array;
        final int idx = i >>> 3;
        final int shift = (i & 7) << 3;
        return (byte) (((long) LONG_ARRAY_HANDLE.getOpaque(array, idx) >> shift) & 0xFFL);
    }

    public boolean compareAndSet(int i, byte expect, byte newValue) {
        final long[] array = this.array;
        int idx = i >>> 3;
        int shift = (i & 7) << 3;
        long mask = 0xFFL << shift;
        long expected = (expect & 0xFFL) << shift;
        long valueToInject = (newValue & 0xFFL) << shift;

        long num2, num;
        for (;;) {
            num = (long) LONG_ARRAY_HANDLE.getOpaque(array, idx);

            if ((num & mask) != expected) return false;

            num2 = (num & ~mask) | valueToInject;
            if (LONG_ARRAY_HANDLE.weakCompareAndSetPlain(array, idx, num, num2)) break;
            Thread.onSpinWait();
        }
        CACHED_ARRAY_HANDLE.setOpaque(this, null);
        return true;
    }

    @VisibleForTesting
    public byte incrementAndGet(final int i) {
        byte old, newValue;
        for (;;) {
            old = get(i);
            newValue = (byte) (old + 1);
            if (compareAndSet(i, old, newValue)) break;
            Thread.onSpinWait();
        }
        return newValue;
    }

    public int length() {
        return this.length;
    }

    public byte[] getCopy() {
        byte[] arr = (byte[]) CACHED_ARRAY_HANDLE.getOpaque(this);
        if (arr == null) {
            byte[] fresh = new byte[this.length];
            long[] array = this.array;
            int fullWords = fresh.length >>> 3;
            for (int idx = 0; idx < fullWords; idx++) {
                long word = (long) LONG_ARRAY_HANDLE.getOpaque(array, idx);
                BYTE_LONG_VIEW.set(fresh, idx, word);
            }
            int tail = fresh.length & 7;
            if (tail != 0) {
                long word = (long) LONG_ARRAY_HANDLE.getOpaque(array, fullWords);
                int base = fullWords << 3;
                for (int shift = 0; shift < tail; shift++) {
                    fresh[base + shift] = (byte) (word >>> (shift << 3));
                }
            }
            if (CACHED_ARRAY_HANDLE.weakCompareAndSetPlain(this, null, fresh)) {
                arr = fresh;
            } else {
                arr = (byte[]) CACHED_ARRAY_HANDLE.getOpaque(this);
                if (arr == null) {
                    arr = fresh;
                }
            }
        }
        return arr;
    }

    public void setAll0() {
        for (int idx = 0; idx < array.length; idx++) {
            LONG_ARRAY_HANDLE.setOpaque(array, idx, 0L);
        }
        CACHED_ARRAY_HANDLE.setOpaque(this, null);
    }
}
