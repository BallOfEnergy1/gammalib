package com.gamma.gammalib.multi.bytearray;

import com.gamma.gammalib.multi.FastImpl;

public interface FastAtomicByteArray extends FastImpl {

    byte get(int idx);

    void set(int idx, byte value);

    boolean compareAndSet(int idx, byte expect, byte newValue);

    byte incrementAndGet(int idx);

    byte[] getCopy();

    void setAll0();

    int length();
}
