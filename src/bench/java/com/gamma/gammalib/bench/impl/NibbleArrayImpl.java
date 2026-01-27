package com.gamma.gammalib.bench.impl;

import com.gamma.gammalib.bench.ImplConfigScope;
import com.gamma.gammalib.bench.misc.MCNibbleArrayProxy;
import com.gamma.gammalib.multi.MultiJavaUtil;
import com.gamma.gammalib.multi.factory.NibbleArrayFactory;
import com.gamma.gammalib.multi.nibblearray.FastAtomicNibbleArray;

public enum NibbleArrayImpl {

    MC("nibblearray-minecraft", 8, false),
    JAVA_8_SAFE("nibblearray-8-safe", 8, false),
    JAVA_8("nibblearray-8", 8, true),
    JAVA_9("nibblearray-9", 9, false);

    private final String label;
    public final int version;
    public final boolean useUnsafe;

    NibbleArrayImpl(String label, int version, boolean useUnsafe) {
        this.label = label;
        this.version = version;
        this.useUnsafe = useUnsafe;
    }

    public String label() {
        return label;
    }

    public boolean isSupported() {
        return MultiJavaUtil.supportsVersion(version);
    }

    public ImplConfigScope applyConfig() {
        return new ImplConfigScope(version, useUnsafe, false);
    }

    public FastAtomicNibbleArray create(int size) {
        if (this == MC) return new MCNibbleArrayProxy(size);
        return NibbleArrayFactory.create(size);
    }
}
