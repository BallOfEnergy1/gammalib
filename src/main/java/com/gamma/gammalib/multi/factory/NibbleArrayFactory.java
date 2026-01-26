package com.gamma.gammalib.multi.factory;

import com.gamma.gammalib.config.ImplConfig;
import com.gamma.gammalib.multi.MultiJavaUtil;
import com.gamma.gammalib.multi.nibblearray.AtomicNibbleArray8;
import com.gamma.gammalib.multi.nibblearray.AtomicNibbleArray8Safe;
import com.gamma.gammalib.multi.nibblearray.AtomicNibbleArray9;
import com.gamma.gammalib.multi.nibblearray.FastAtomicNibbleArray;
import com.gamma.gammalib.unsafe.UnsafeAccessor;

public class NibbleArrayFactory {

    public static FastAtomicNibbleArray create(int size) {
        if (MultiJavaUtil.hasJava9Support() && ImplConfig.useJava9Features) {
            return new AtomicNibbleArray9(size);
        }
        if (ImplConfig.useUnsafe && UnsafeAccessor.ENABLED) {
            if (UnsafeAccessor.IS_AVAILABLE || UnsafeAccessor.getUnsafe() != null) return new AtomicNibbleArray8(size);
        }
        return new AtomicNibbleArray8Safe(size);
    }
}
