package com.gamma.gammalib.bench;

import com.gamma.gammalib.config.ImplConfig;
import com.gamma.gammalib.unsafe.UnsafeAccessor;

public final class ImplConfigScope implements AutoCloseable {

    private final boolean prevUnsafe;
    private final boolean prevJava9;
    private final boolean prevJava17;
    private final boolean prevJava25;
    private final boolean prevCompact;

    public ImplConfigScope(int version, boolean useUnsafe, boolean useCompact) {
        this.prevUnsafe = ImplConfig.useUnsafe;
        this.prevJava9 = ImplConfig.useJava9Features;
        this.prevJava17 = ImplConfig.useJava17Features;
        this.prevJava25 = ImplConfig.useJava25Features;
        this.prevCompact = ImplConfig.useCompactImpls;

        ImplConfig.useUnsafe = useUnsafe;
        if (useUnsafe) UnsafeAccessor.enableUnsafe();
        else UnsafeAccessor.disableUnsafe();
        ImplConfig.useJava9Features = version >= 9;
        ImplConfig.useJava17Features = version >= 17;
        ImplConfig.useJava25Features = version >= 25;
        ImplConfig.useCompactImpls = useCompact;
    }

    @Override
    public void close() {
        ImplConfig.useUnsafe = prevUnsafe;
        if (!prevUnsafe && UnsafeAccessor.ENABLED) {
            UnsafeAccessor.disableUnsafe();
        } else if (prevUnsafe && !UnsafeAccessor.ENABLED) {
            UnsafeAccessor.enableUnsafe();
        }
        ImplConfig.useJava9Features = prevJava9;
        ImplConfig.useJava17Features = prevJava17;
        ImplConfig.useJava25Features = prevJava25;
        ImplConfig.useCompactImpls = prevCompact;
    }
}
