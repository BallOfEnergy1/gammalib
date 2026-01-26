package com.gamma.gammalib.multi.bitset;

import java.util.concurrent.locks.StampedLock;

public class AtomicBitSetCompact implements FastAtomicBitSet {

    private final StampedLock lock = new StampedLock();
    private static final int BITS_PER_WORD = 64;

    private final long[] array;

    private final int length;

    public AtomicBitSetCompact(int bitsLength) {
        length = bitsLength;
        array = new long[(bitsLength + (BITS_PER_WORD - 1)) >>> 6];
    }

    @Override
    public void set(int bitIndex) {
        long stamp = lock.writeLock();
        int idx = bitIndex >> 6;
        long bit = 1L << (bitIndex & (BITS_PER_WORD - 1));
        try {
            array[idx] |= bit;
        } finally {
            lock.unlockWrite(stamp);
        }
    }

    @Override
    public void clear(int bitIndex) {
        long stamp = lock.writeLock();
        int idx = bitIndex >> 6;
        long bit = 1L << (bitIndex & (BITS_PER_WORD - 1));
        try {
            array[idx] &= ~bit;
        } finally {
            lock.unlockWrite(stamp);
        }
    }

    @Override
    public void set(int bitIndex, boolean value) {
        if (value) set(bitIndex);
        else clear(bitIndex);
    }

    @Override
    public void flip(int bitIndex) {
        int idx = bitIndex >> 6;
        long bit = 1L << (bitIndex & (BITS_PER_WORD - 1));
        long stamp = lock.writeLock();
        try {
            array[idx] ^= bit;
        } finally {
            lock.unlockWrite(stamp);
        }
    }

    @Override
    public boolean get(int bitIndex) {
        int idx = bitIndex >> 6;
        long bit = 1L << (bitIndex & (BITS_PER_WORD - 1));
        long stamp = lock.readLock();
        try {
            return get0(idx, bit);
        } finally {
            lock.unlockRead(stamp);
        }
    }

    private boolean get0(int idx, long bit) {
        return (array[idx] & bit) != 0;
    }

    @Override
    public int length() {
        return length;
    }
}
