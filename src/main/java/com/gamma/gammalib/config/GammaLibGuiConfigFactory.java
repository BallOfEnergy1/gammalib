package com.gamma.gammalib.config;

import net.minecraft.client.gui.GuiScreen;

import com.gtnewhorizon.gtnhlib.config.SimpleGuiFactory;

@SuppressWarnings("unused")
public class GammaLibGuiConfigFactory implements SimpleGuiFactory {

    @Override
    public Class<? extends GuiScreen> mainConfigGuiClass() {
        return GammaLibGuiConfig.class;
    }
}
