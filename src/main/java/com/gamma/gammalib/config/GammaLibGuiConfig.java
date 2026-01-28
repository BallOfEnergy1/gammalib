package com.gamma.gammalib.config;

import net.minecraft.client.gui.GuiScreen;

import com.gtnewhorizon.gtnhlib.config.ConfigException;
import com.gtnewhorizon.gtnhlib.config.SimpleGuiConfig;

public class GammaLibGuiConfig extends SimpleGuiConfig {

    public GammaLibGuiConfig(GuiScreen parent) throws ConfigException {
        super(parent, "gammalib", "GammaLib", true, DebugConfig.class, ImplConfig.class);
    }
}
