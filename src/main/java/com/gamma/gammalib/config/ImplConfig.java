package com.gamma.gammalib.config;

import com.gtnewhorizon.gtnhlib.config.Config;

@Config(modid = "gammalib", category = "implementation")
@Config.Comment("Implementation config.")
public class ImplConfig {

    @Config.Comment("Enables use of Java 8 Unsafe (if compatibility is detected) to improve performance under thread contention (volatiles, fencing, etc.).")
    @Config.DefaultBoolean(true)
    @Config.Name("Use Java 8 Unsafe?")
    public static boolean useUnsafe;

    @Config.Comment("Enables use of Java 9+ features if compatibility is detected (LWJGL3ify).")
    @Config.DefaultBoolean(true)
    @Config.Name("Use Java 9+ features?")
    public static boolean useJava9Features;

    @Config.Comment("Enables use of Java 17+ features if compatibility is detected (LWJGL3ify). Overrides `B:useJava9Features`.")
    @Config.DefaultBoolean(true)
    @Config.Name("Use Java 17+ features?")
    public static boolean useJava17Features;

    @Config.Comment("Enables use of Java 25+ features if compatibility is detected (LWJGL3ify). Overrides `B:useJava17Features`.")
    @Config.DefaultBoolean(true)
    @Config.Name("Use Java 25+ features?")
    public static boolean useJava25Features;

    @Config.Comment("If memory efficiency should be prioritized over speed, sometimes sacrificing performance for a large reduction in RAM usage by using \"compact\" implementations.")
    @Config.DefaultBoolean(false)
    @Config.Name("Use compact implementations?")
    public static boolean useCompactImpls;
}
