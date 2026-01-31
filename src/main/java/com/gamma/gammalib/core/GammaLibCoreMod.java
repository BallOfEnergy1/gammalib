package com.gamma.gammalib.core;

import java.lang.instrument.Instrumentation;
import java.util.Collection;
import java.util.Map;

import net.bytebuddy.agent.ByteBuddyAgent;

import com.gamma.gammalib.config.DebugConfig;
import com.gamma.gammalib.config.ImplConfig;
import com.gamma.gammalib.multi.MultiJavaUtil;
import com.gamma.gammalib.unsafe.UnsafeAccessor;
import com.gtnewhorizon.gtnhlib.config.ConfigException;
import com.gtnewhorizon.gtnhlib.config.ConfigurationManager;

import cpw.mods.fml.relauncher.IFMLLoadingPlugin;

@IFMLLoadingPlugin.MCVersion("1.7.10")
@IFMLLoadingPlugin.TransformerExclusions({
    "com.gamma.gammalib.",
    "com.gtnewhorizon.gtnhlib.asm",
    "it.unimi.dsi.fastutil"
}) // go away.
public class GammaLibCoreMod implements IFMLLoadingPlugin {

    private static Instrumentation instrumentation;

    public static boolean isObfuscatedEnv;

    public static long getRecursiveObjectSize(Object o) {
        if (!OBJECT_DEBUG) return -1;
        if (o == null) return 0;
        long size = 0;

        if (o instanceof Collection<?>) {
            for (Object obj : ((Collection<?>) o)) {
                // This doesn't work great for objects containing lists;
                // it only works if the object itself is a list.
                size += getRecursiveObjectSize(obj);
            }
        } else if (o instanceof Map<?, ?>) {
            for (Map.Entry<?, ?> obj : ((Map<?, ?>) o).entrySet()) {
                // This doesn't work great for objects containing lists;
                // it only works if the object itself is a list.
                size += getRecursiveObjectSize(obj.getKey());
                size += getRecursiveObjectSize(obj.getValue());
            }
        }

        return size + instrumentation.getObjectSize(o); // include class overhead
    }

    public static final boolean OBJECT_DEBUG = false;

    static {
        try {
            ConfigurationManager.registerConfig(DebugConfig.class);
            ConfigurationManager.registerConfig(ImplConfig.class);

            boolean isUnsafeDeprecated = MultiJavaUtil.supportsVersion(23);
            if (ImplConfig.useUnsafe && !isUnsafeDeprecated) UnsafeAccessor.init();

            GammaLibLogger.info("================== Available Java Features =================");
            GammaLibLogger.info("\tDetected Java version: " + MultiJavaUtil.getVersion());
            GammaLibLogger.info(
                "\tJava 8 Unsafe: Enabled: " + ImplConfig.useUnsafe + "; Available: " + UnsafeAccessor.IS_AVAILABLE);
            if (isUnsafeDeprecated)
                GammaLibLogger.warn("\tJava Unsafe is deprecated as of Java 23 and will not be used!");
            GammaLibLogger.info(
                "\tJava >= 9: Enabled: " + ImplConfig.useJava9Features
                    + "; Supported: "
                    + MultiJavaUtil.hasJava9Support());
            GammaLibLogger.info(
                "\tJava >= 17: Enabled: " + ImplConfig.useJava17Features
                    + "; Supported: "
                    + MultiJavaUtil.hasJava17Support());
            GammaLibLogger.info(
                "\tJava >= 25: Enabled: " + ImplConfig.useJava25Features
                    + "; Supported: "
                    + MultiJavaUtil.hasJava25Support());
            GammaLibLogger.info("\tCompact impls: Enabled: " + ImplConfig.useCompactImpls);
            GammaLibLogger.info("============================================================");

            if (OBJECT_DEBUG) {
                // Debug code that allows us to dynamically load the instrumentation agent.
                // This should always be disabled unless you *really* **really** need it.
                GammaLibLogger.warn("!!!Object debug enabled!!!");
                try {
                    instrumentation = ByteBuddyAgent.install();
                    GammaLibLogger.warn("Successfully loaded instrumentation agent!");
                    GammaLibLogger.warn(
                        "Instrumentation test: `new Object()` is " + instrumentation.getObjectSize(new Object())
                            + " bytes.");
                } catch (Exception ignored) {}
            }
        } catch (ConfigException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String[] getASMTransformerClass() {
        return new String[] { "com.gamma.gammalib.asm.PrimaryTransformerHandler" };
    }

    @Override
    public String getModContainerClass() {
        return null;
    }

    @Override
    public String getSetupClass() {
        return null;
    }

    @Override
    public void injectData(Map<String, Object> data) {
        isObfuscatedEnv = !(boolean) data.get("runtimeDeobfuscationEnabled");
    }

    @Override
    public String getAccessTransformerClass() {
        return null;
    }
}
