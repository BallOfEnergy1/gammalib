package com.gamma.gammalib.core;

import com.gamma.gammalib.util.early.EarlyConfig;

@EarlyConfig("gammalib")
public class GammaLibConfig {

    @EarlyConfig.DefaultBoolean(true)
    @EarlyConfig.Name("impl.useUnsafe")
    public static boolean useUnsafe;

    @EarlyConfig.DefaultBoolean(true)
    @EarlyConfig.Name("impl.useSIMD")
    public static boolean useSIMDNatives;

    @EarlyConfig.DefaultBoolean(true)
    @EarlyConfig.Name("impl.useJava9")
    public static boolean useJava9Features;

    @EarlyConfig.DefaultBoolean(true)
    @EarlyConfig.Name("impl.useJava17")
    public static boolean useJava17Features;

    @EarlyConfig.DefaultBoolean(true)
    @EarlyConfig.Name("impl.useJava25")
    public static boolean useJava25Features;

    @EarlyConfig.DefaultBoolean(false)
    @EarlyConfig.Name("impl.preferCompact")
    public static boolean useCompactImpls;

    @EarlyConfig.DefaultBoolean(false)
    @EarlyConfig.Name("debug.logging")
    public static boolean debugLogging;

    @EarlyConfig.DefaultBoolean(false)
    @EarlyConfig.Name("debug.compatibility.logging")
    public static boolean compatLogging;

    @EarlyConfig.DefaultBoolean(false)
    @EarlyConfig.Name("debug.asm.logging")
    public static boolean logASM;
}
