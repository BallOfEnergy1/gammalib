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
    JAVA_9("nibblearray-9", 9, false),
    WRAPPED_JAVA_8_SAFE("nibblearray-wrapped-8-safe", 8, false, true),
    WRAPPED_JAVA_8("nibblearray-wrapped-8", 8, true, true),
    WRAPPED_JAVA_9("nibblearray-wrapped-9", 9, false, true);

    private final String label;
    public final int version;
    public final boolean useUnsafe;
    public final boolean wrapped;

    NibbleArrayImpl(String label, int version, boolean useUnsafe) {
        this(label, version, useUnsafe, false);
    }

    NibbleArrayImpl(String label, int version, boolean useUnsafe, boolean wrapped) {
        this.label = label;
        this.version = version;
        this.useUnsafe = useUnsafe;
        this.wrapped = wrapped;
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
        if (wrapped) {
            byte[] array = new byte[(size + 1) >>> 1];
            return NibbleArrayFactory.wrap(array);
        }
        return NibbleArrayFactory.create(size);
    }
}
