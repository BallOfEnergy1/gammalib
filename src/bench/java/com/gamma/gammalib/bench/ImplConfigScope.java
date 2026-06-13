package com.gamma.gammalib.bench;

import com.gamma.gammalib.core.GammaLibConfig;
import com.gamma.gammalib.unsafe.UnsafeAccessor;

public final class ImplConfigScope implements AutoCloseable {

    private final boolean prevUnsafe;
    private final boolean prevJava9;
    private final boolean prevJava17;
    private final boolean prevJava25;
    private final boolean prevCompact;

    public ImplConfigScope(int version, boolean useUnsafe, boolean useCompact) {
        this.prevUnsafe = GammaLibConfig.useUnsafe;
        this.prevJava9 = GammaLibConfig.useJava9Features;
        this.prevJava17 = GammaLibConfig.useJava17Features;
        this.prevJava25 = GammaLibConfig.useJava25Features;
        this.prevCompact = GammaLibConfig.useCompactImpls;

        GammaLibConfig.useUnsafe = useUnsafe;
        if (useUnsafe) UnsafeAccessor.enableUnsafe();
        else UnsafeAccessor.disableUnsafe();
        GammaLibConfig.useJava9Features = version >= 9;
        GammaLibConfig.useJava17Features = version >= 17;
        GammaLibConfig.useJava25Features = version >= 25;
        GammaLibConfig.useCompactImpls = useCompact;
    }

    @Override
    public void close() {
        GammaLibConfig.useUnsafe = prevUnsafe;
        if (!prevUnsafe && UnsafeAccessor.ENABLED) {
            UnsafeAccessor.disableUnsafe();
        } else if (prevUnsafe && !UnsafeAccessor.ENABLED) {
            UnsafeAccessor.enableUnsafe();
        }
        GammaLibConfig.useJava9Features = prevJava9;
        GammaLibConfig.useJava17Features = prevJava17;
        GammaLibConfig.useJava25Features = prevJava25;
        GammaLibConfig.useCompactImpls = prevCompact;
    }
}
