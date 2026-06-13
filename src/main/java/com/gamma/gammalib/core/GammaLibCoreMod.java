package com.gamma.gammalib.core;

import java.lang.instrument.Instrumentation;
import java.util.Collection;
import java.util.Map;

import net.bytebuddy.agent.ByteBuddyAgent;

import com.gamma.gammalib.multi.MultiJavaUtil;
import com.gamma.gammalib.unsafe.UnsafeAccessor;
import com.gamma.gammalib.util.early.EarlyConfigManager;

import cpw.mods.fml.relauncher.IFMLLoadingPlugin;

@IFMLLoadingPlugin.MCVersion("1.7.10")
@IFMLLoadingPlugin.TransformerExclusions({ "com.gamma.gammalib.", "com.gtnewhorizon.gtnhlib.asm",
    "com.gtnewhorizon.gtnhlib.config", "it.unimi.dsi.fastutil" })
public class GammaLibCoreMod implements IFMLLoadingPlugin {

    private static Instrumentation instrumentation;

    public static boolean isObfuscatedEnv;

    public static final boolean OBJECT_DEBUG = false;

    /**
     * Utility for recursively getting the size of a Java object, somewhat-not-so-reliably. This should only
     * be used when `OBJECT_DEBUG` is true, and only for rough estimations/approximations, as it will almost never
     * be fully accurate!
     *
     * @param o Object to get the size of.
     * @return The approximate size of the object, in bytes.
     */
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
                size += getRecursiveObjectSize(obj.getKey());
                size += getRecursiveObjectSize(obj.getValue());
            }
        }

        return size + instrumentation.getObjectSize(o); // include class overhead
    }

    static {
        EarlyConfigManager.registerConfig(GammaLibConfig.class);

        boolean isUnsafeDeprecated = MultiJavaUtil.supportsVersion(23);
        if (GammaLibConfig.useUnsafe && !isUnsafeDeprecated) UnsafeAccessor.init();

        GammaLibLogger.info("================== Available Java Features =================");
        GammaLibLogger.info("\tDetected Java version: " + MultiJavaUtil.getVersion());
        GammaLibLogger.info(
            "\tJava 8 Unsafe: Enabled: " + GammaLibConfig.useUnsafe + "; Available: " + UnsafeAccessor.IS_AVAILABLE);
        if (isUnsafeDeprecated) GammaLibLogger.warn("\tJava Unsafe is deprecated as of Java 23 and will not be used!");
        GammaLibLogger.info(
            "\tJava >= 9: Enabled: " + GammaLibConfig.useJava9Features
                + "; Supported: "
                + MultiJavaUtil.hasJava9Support());
        GammaLibLogger.info(
            "\tJava >= 17: Enabled: " + GammaLibConfig.useJava17Features
                + "; Supported: "
                + MultiJavaUtil.hasJava17Support());
        GammaLibLogger.info(
            "\tJava >= 25: Enabled: " + GammaLibConfig.useJava25Features
                + "; Supported: "
                + MultiJavaUtil.hasJava25Support());
        GammaLibLogger.info("\tCompact impls: Enabled: " + GammaLibConfig.useCompactImpls);
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
        isObfuscatedEnv = (boolean) data.get("runtimeDeobfuscationEnabled");
        GammaLibLogger.info("Obfuscation enabled: " + isObfuscatedEnv);
    }

    @Override
    public String getAccessTransformerClass() {
        return null;
    }
}
