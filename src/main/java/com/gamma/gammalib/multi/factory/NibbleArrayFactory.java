package com.gamma.gammalib.multi.factory;

import com.gamma.gammalib.core.GammaLibConfig;
import com.gamma.gammalib.multi.MultiJavaUtil;
import com.gamma.gammalib.multi.nibblearray.AtomicNibbleArray8;
import com.gamma.gammalib.multi.nibblearray.AtomicNibbleArray8Safe;
import com.gamma.gammalib.multi.nibblearray.AtomicNibbleArray9;
import com.gamma.gammalib.multi.nibblearray.FastAtomicNibbleArray;
import com.gamma.gammalib.multi.nibblearray.WrappedAtomicNibbleArray8;
import com.gamma.gammalib.multi.nibblearray.WrappedAtomicNibbleArray8Safe;
import com.gamma.gammalib.multi.nibblearray.WrappedAtomicNibbleArray9;
import com.gamma.gammalib.unsafe.UnsafeAccessor;

public class NibbleArrayFactory {

    public static FastAtomicNibbleArray create(int size) {
        if (MultiJavaUtil.hasJava9Support() && GammaLibConfig.useJava9Features) {
            return new AtomicNibbleArray9(size);
        }
        if (GammaLibConfig.useUnsafe && UnsafeAccessor.ENABLED) {
            if (UnsafeAccessor.IS_AVAILABLE || UnsafeAccessor.getUnsafe() != null) return new AtomicNibbleArray8(size);
        }
        return new AtomicNibbleArray8Safe(size);
    }

    public static FastAtomicNibbleArray wrap(byte[] array) {
        if (MultiJavaUtil.hasJava9Support() && GammaLibConfig.useJava9Features) {
            return new WrappedAtomicNibbleArray9(array);
        }
        if (GammaLibConfig.useUnsafe && UnsafeAccessor.ENABLED) {
            if (UnsafeAccessor.IS_AVAILABLE || UnsafeAccessor.getUnsafe() != null)
                return new WrappedAtomicNibbleArray8(array);
        }
        return new WrappedAtomicNibbleArray8Safe(array);
    }
}
