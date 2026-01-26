package com.gamma.gammalib.multi.nibblearray;

import com.gamma.gammalib.multi.FastImpl;
import com.google.common.annotations.VisibleForTesting;

public interface FastAtomicNibbleArray extends FastImpl {

    default int get(int x, int y, int z) {
        return get(y << 8 | z << 4 | x);
    }

    int get(int idx);

    default void set(int x, int y, int z, int value) {
        set(y << 8 | z << 4 | x, value);
    }

    void set(int idx, int value);

    @VisibleForTesting
    int incrementAndGet(int idx);

    // byte[] getByteArray();
}
