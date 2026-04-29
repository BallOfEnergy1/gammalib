package com.gamma.gammalib.core;

import com.gamma.gammalib.Tags;
import com.gtnewhorizon.gtnhlib.eventbus.EventBusSubscriber;

import cpw.mods.fml.common.Mod;

@Mod(
    modid = GammaLib.MODID,
    version = Tags.VERSION,
    dependencies = "required-after:gtnhmixins@[2.0.1,);" + "required-after:unimixins@[0.0.20,);"
        + "required-after:gtnhlib@[0.6.21,);",
    guiFactory = "com.gamma.gammalib.config.GammaLibGuiConfigFactory",
    acceptedMinecraftVersions = "[1.7.10]",
    acceptableRemoteVersions = "*")
@EventBusSubscriber
public class GammaLib {

    public static final String MODID = "gammalib";
    public static final String VERSION = Tags.VERSION;

    @Mod.Instance(MODID)
    public static GammaLib instance;
}
