package com.gamma.gammalib.multi.bitset;

import com.gamma.gammalib.multi.FastImpl;

public interface FastAtomicBitSet extends FastImpl {

    boolean get(int bitIndex);

    void set(int bitIndex, boolean value);

    void set(int bitIndex);

    void clear(int bitIndex);

    void flip(int bitIndex);

    int length();
}
