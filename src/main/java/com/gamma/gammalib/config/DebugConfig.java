package com.gamma.gammalib.config;

import com.gtnewhorizon.gtnhlib.config.Config;

@Config(modid = "gammalib", category = "Debug")
@Config.Comment("Debug config.")
public class DebugConfig {

    @Config.Comment("Enables debug (console logging).")
    @Config.DefaultBoolean(false)
    @Config.Name("Enable debug mode (console logging)?")
    public static boolean debugLogging;

    @Config.Comment("Enables compatibility debug (console logging).")
    @Config.DefaultBoolean(false)
    @Config.Name("Enable compatibility debug mode (console logging)?")
    public static boolean compatLogging;

    @Config.Comment("Enables ASM debug (console logging).")
    @Config.DefaultBoolean(false)
    @Config.Name("Enable ASM debug mode (console logging)?")
    public static boolean logASM;
}
