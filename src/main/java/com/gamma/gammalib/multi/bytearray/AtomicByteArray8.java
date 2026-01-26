package com.gamma.gammalib.multi.bytearray;

import sun.misc.Unsafe;

public class AtomicByteArray8 implements FastAtomicByteArray {

    private static final int BYTES_PER_LONG = 8;

    private static volatile boolean OFFSETS_READY;
    private static long LONG_BASE;
    private static int LONG_SHIFT;
    private static long CACHED_ARRAY_OFFSET;

    private final Unsafe unsafe;
    private final long[] array;
    private final int length;

    private volatile byte[] cachedArray;

    public AtomicByteArray8(final int length) {
        Unsafe u = getUnsafe();
        if (u == null) {
            throw new IllegalStateException("Unsafe is unavailable");
        }
        this.unsafe = u;
        initOffsets(u);
        this.length = length;
        this.array = new long[(length + 7) / BYTES_PER_LONG];
    }

    public void set(final int i, final byte newValue) {
        final int idx = i >>> 3;
        final int shift = (i & 7) << 3;
        final long mask = 0xFFL << shift;
        final long valueToInject = (newValue & 0xFFL) << shift;

        final long offset = longOffset(idx);
        long oldLong;
        long newLong;
        do {
            oldLong = unsafe.getLongVolatile(array, offset);
            newLong = (oldLong & ~mask) | valueToInject;
        } while (!unsafe.compareAndSwapLong(array, offset, oldLong, newLong));
        unsafe.putOrderedObject(this, CACHED_ARRAY_OFFSET, null);
    }

    public byte get(final int i) {
        long word = unsafe.getLongVolatile(array, longOffset(i >>> 3));
        return (byte) ((word >>> ((i & 7) << 3)) & 0xFFL);
    }

    public boolean compareAndSet(int i, byte expect, byte newValue) {
        int idx = i >>> 3;
        int shift = (i & 7) << 3;
        long mask = 0xFFL << shift;
        long expected = (expect & 0xFFL) << shift;
        long valueToInject = (newValue & 0xFFL) << shift;

        final long offset = longOffset(idx);
        long num;
        long num2;
        do {
            num = unsafe.getLongVolatile(array, offset);
            if ((num & mask) != expected) return false;
            num2 = (num & ~mask) | valueToInject;
        } while (!unsafe.compareAndSwapLong(array, offset, num, num2));
        unsafe.putOrderedObject(this, CACHED_ARRAY_OFFSET, null);
        return true;
    }

    public byte incrementAndGet(final int i) {
        byte old;
        byte newValue;
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
        byte[] arr = cachedArray;
        if (arr == null) {
            byte[] fresh = new byte[this.length];
            int fullWords = fresh.length >>> 3;
            for (int idx = 0; idx < fullWords; idx++) {
                long word = unsafe.getLongVolatile(array, longOffset(idx));
                int base = idx << 3;
                fresh[base] = (byte) (word);
                fresh[base + 1] = (byte) (word >>> 8);
                fresh[base + 2] = (byte) (word >>> 16);
                fresh[base + 3] = (byte) (word >>> 24);
                fresh[base + 4] = (byte) (word >>> 32);
                fresh[base + 5] = (byte) (word >>> 40);
                fresh[base + 6] = (byte) (word >>> 48);
                fresh[base + 7] = (byte) (word >>> 56);
            }
            int tail = fresh.length & 7;
            if (tail != 0) {
                long word = unsafe.getLongVolatile(array, longOffset(fullWords));
                int base = fullWords << 3;
                for (int shift = 0; shift < tail; shift++) {
                    fresh[base + shift] = (byte) (word >>> (shift << 3));
                }
            }
            if (unsafe.compareAndSwapObject(this, CACHED_ARRAY_OFFSET, null, fresh)) {
                arr = fresh;
            } else {
                arr = cachedArray;
                if (arr == null) {
                    arr = fresh;
                }
            }
        }
        return arr;
    }

    public void setAll0() {
        for (int idx = 0; idx < array.length; idx++) {
            unsafe.putOrderedLong(array, longOffset(idx), 0L);
        }
        unsafe.putOrderedObject(this, CACHED_ARRAY_OFFSET, null);
    }

    private static void initOffsets(Unsafe unsafe) {
        if (OFFSETS_READY) return;
        synchronized (AtomicByteArray8.class) {
            if (OFFSETS_READY) return;
            try {
                LONG_BASE = unsafe.arrayBaseOffset(long[].class);
                int scale = unsafe.arrayIndexScale(long[].class);
                if (scale != BYTES_PER_LONG) {
                    throw new ExceptionInInitializerError("Unexpected long[] scale: " + scale);
                }
                LONG_SHIFT = 3;
                CACHED_ARRAY_OFFSET = unsafe.objectFieldOffset(AtomicByteArray8.class.getDeclaredField("cachedArray"));
                OFFSETS_READY = true;
            } catch (NoSuchFieldException e) {
                throw new ExceptionInInitializerError(e);
            }
        }
    }

    private static long longOffset(int index) {
        return LONG_BASE + ((long) index << LONG_SHIFT);
    }
}
