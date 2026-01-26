package com.gamma.gammalib.multi.bytearray;

import java.util.concurrent.atomic.AtomicLongArray;
import java.util.concurrent.atomic.AtomicReference;

import com.google.common.annotations.VisibleForTesting;

public class AtomicByteArray8Safe implements FastAtomicByteArray {

    private static final int BYTES_PER_LONG = 8;

    private final AtomicLongArray array;
    private final int length;
    private final AtomicReference<byte[]> cachedArray = new AtomicReference<>();

    public AtomicByteArray8Safe(final int length) {
        this.length = length;
        this.array = new AtomicLongArray((length + 7) / BYTES_PER_LONG);
    }

    public void set(final int i, final byte newValue) {
        final int idx = i >>> 3;
        final int shift = (i & 7) << 3;
        final long mask = 0xFFL << shift;
        final long valueToInject = (newValue & 0xFFL) << shift;

        long oldLong, newLong;
        do {
            oldLong = this.array.get(idx);
            newLong = (oldLong & ~mask) | valueToInject;
        } while (!this.array.compareAndSet(idx, oldLong, newLong));
        cachedArray.lazySet(null);
    }

    public byte get(final int i) {
        return (byte) ((this.array.get(i >>> 3) >> ((i & 7) << 3)) & 0xFFL);
    }

    public boolean compareAndSet(int i, byte expect, byte newValue) {
        int idx = i >>> 3;
        int shift = (i & 7) << 3;
        long mask = 0xFFL << shift;
        long expected = (expect & 0xFFL) << shift;
        long valueToInject = (newValue & 0xFFL) << shift;

        long num2, num;
        do {
            num = this.array.get(idx);
            if ((num & mask) != expected) return false;
            num2 = (num & ~mask) | valueToInject;
        } while (!this.array.compareAndSet(idx, num, num2));
        cachedArray.lazySet(null);
        return true;
    }

    @VisibleForTesting
    public byte incrementAndGet(final int i) {
        byte old, newValue;
        do {
            old = get(i);
            newValue = (byte) (old + 1);
        } while (!compareAndSet(i, old, newValue));
        return newValue;
    }

    public int length() {
        return this.length;
    }

    public byte[] getCopy() {
        byte[] arr = cachedArray.get();
        if (arr == null) {
            byte[] fresh = new byte[this.length];
            int fullWords = fresh.length >>> 3;
            int out = 0;
            for (int idx = 0; idx < fullWords; idx++) {
                long word = array.get(idx);
                fresh[out++] = (byte) (word);
                fresh[out++] = (byte) (word >>> 8);
                fresh[out++] = (byte) (word >>> 16);
                fresh[out++] = (byte) (word >>> 24);
                fresh[out++] = (byte) (word >>> 32);
                fresh[out++] = (byte) (word >>> 40);
                fresh[out++] = (byte) (word >>> 48);
                fresh[out++] = (byte) (word >>> 56);
            }
            for (int idx = out; idx < fresh.length; idx++) {
                fresh[idx] = this.get(idx);
            }
            if (cachedArray.compareAndSet(null, fresh)) {
                arr = fresh;
            } else {
                arr = cachedArray.get();
                if (arr == null) {
                    arr = fresh;
                }
            }
        }
        return arr;
    }

    public void setAll0() {
        for (int idx = 0; idx < array.length(); idx++) {
            array.set(idx, 0L);
        }
        cachedArray.lazySet(null);
    }
}
