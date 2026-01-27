package com.gamma.gammalib.bench.impl;

import com.gamma.gammalib.bench.ImplConfigScope;
import com.gamma.gammalib.multi.MultiJavaUtil;
import com.gamma.gammalib.multi.bytearray.FastAtomicByteArray;
import com.gamma.gammalib.multi.factory.ByteArrayFactory;

public enum ByteArrayImpl {

    JAVA_8_SAFE("bytearray-8-safe", 8, false),
    JAVA_8("bytearray-8", 8, true),
    JAVA_9("bytearray-9", 9, false);

    private final String label;
    public final int version;
    public final boolean useUnsafe;

    ByteArrayImpl(String label, int version, boolean useUnsafe) {
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

    public FastAtomicByteArray create(int size) {
        return ByteArrayFactory.create(size);
    }
}
