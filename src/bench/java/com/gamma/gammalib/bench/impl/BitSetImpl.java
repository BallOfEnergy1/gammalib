package com.gamma.gammalib.bench.impl;

import com.gamma.gammalib.bench.ImplConfigScope;
import com.gamma.gammalib.bench.misc.JavaBitSetProxy;
import com.gamma.gammalib.multi.MultiJavaUtil;
import com.gamma.gammalib.multi.bitset.FastAtomicBitSet;
import com.gamma.gammalib.multi.factory.BitSetFactory;

public enum BitSetImpl {

    STDLIB("bitset-java", 8, false, false),
    COMPACT("bitset-compact", 8, false, true),
    JAVA_8("bitset-8", 8, false, false),
    JAVA_9("bitset-9", 9, false, false);

    private final String label;
    public final int version;
    public final boolean useUnsafe;
    public final boolean compact;

    BitSetImpl(String label, int version, boolean useUnsafe, boolean useCompact) {
        this.label = label;
        this.version = version;
        this.useUnsafe = useUnsafe;
        this.compact = useCompact;
    }

    public String label() {
        return label;
    }

    public boolean isSupported() {
        return MultiJavaUtil.supportsVersion(version);
    }

    public ImplConfigScope applyConfig() {
        return new ImplConfigScope(version, useUnsafe, compact);
    }

    public FastAtomicBitSet create(int size) {
        if (this == STDLIB) return new JavaBitSetProxy(size);
        return BitSetFactory.create(size);
    }
}
