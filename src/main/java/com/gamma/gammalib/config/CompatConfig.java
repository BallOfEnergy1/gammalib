package com.gamma.gammalib.config;

import com.gtnewhorizon.gtnhlib.config.Config;

@Config(modid = "gammalib", category = "Compatibility")
@Config.Comment("Compatibility config.")
@Config.RequiresMcRestart
public class CompatConfig {

    @Config.Comment("If FQCNs (Fully-Qualified Class Name) checks should be used to determine if some mods are installed early in the mod lifecycle. This may cause issues with some mods if they change classloaders (sudden `java.lang.ClassNotFoundException` on launch).")
    @Config.DefaultBoolean(true)
    @Config.Name("Enable FQCN compatibility checks?")
    public static boolean enableFQCNChecks;

    @Config.Comment("ModIDs that should *always* register as loaded. This should only be used if a mod that you have installed is not properly being recognized.")
    @Config.Name("Force-loaded modIDs")
    public static String[] forceLoadedModIDs;
}
