package com.gamma.gammalib.multi.factory;

import com.gamma.gammalib.config.ImplConfig;
import com.gamma.gammalib.multi.MultiJavaUtil;
import com.gamma.gammalib.multi.bytearray.AtomicByteArray8;
import com.gamma.gammalib.multi.bytearray.AtomicByteArray8Safe;
import com.gamma.gammalib.multi.bytearray.AtomicByteArray9;
import com.gamma.gammalib.multi.bytearray.FastAtomicByteArray;
import com.gamma.gammalib.unsafe.UnsafeAccessor;

public class ByteArrayFactory {

    public static FastAtomicByteArray create(int size) {
        if (MultiJavaUtil.hasJava9Support() && ImplConfig.useJava9Features) {
            return new AtomicByteArray9(size);
        }
        if (ImplConfig.useUnsafe && UnsafeAccessor.ENABLED) {
            if (UnsafeAccessor.IS_AVAILABLE || UnsafeAccessor.getUnsafe() != null) return new AtomicByteArray8(size);
        }
        return new AtomicByteArray8Safe(size);
    }
}
