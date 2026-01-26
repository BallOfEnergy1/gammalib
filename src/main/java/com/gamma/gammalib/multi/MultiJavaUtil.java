package com.gamma.gammalib.multi;

import java.lang.invoke.VarHandle;

public class MultiJavaUtil {

    private static final boolean JAVA_9_COMPATIBLE = hasVarHandleSupport();
    private static final boolean JAVA_17_COMPATIBLE = hasAdvancedVarHandleSupport();
    private static final boolean JAVA_25_COMPATIBLE = hasRuntimeMajorVersion25();
    private static final int VERSION = getVersion0();

    private static boolean hasVarHandleSupport() {
        try {
            Class.forName("java.lang.invoke.VarHandle");
            return true;
        } catch (Throwable e) {
            return false;
        }
    }

    private static boolean hasAdvancedVarHandleSupport() {
        if (!hasVarHandleSupport()) return false;
        try {
            VarHandle.class.getMethod("getAndBitwiseXor", Object[].class);
            VarHandle.class.getMethod("getAndBitwiseOr", Object[].class);
            VarHandle.class.getMethod("getAndBitwiseAnd", Object[].class);
            return true;
        } catch (Throwable e) {
            return false;
        }
    }

    private static boolean hasRuntimeMajorVersion25() {
        if (!hasVarHandleSupport() || !hasAdvancedVarHandleSupport()) return false;
        return getVersion0() >= 25;
    }

    private static int getVersion0() {
        String value = System.getProperty("java.specification.version", "8");
        if (value.startsWith("1.")) {
            value = value.substring(2);
        }
        int dot = value.indexOf('.');
        if (dot > 0) {
            value = value.substring(0, dot);
        }
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException ignored) {
            throw new IllegalStateException("Failed to parse Java version: " + value);
        }
    }

    public static boolean hasJava9Support() {
        return JAVA_9_COMPATIBLE;
    }

    public static boolean hasJava17Support() {
        return JAVA_17_COMPATIBLE;
    }

    public static boolean hasJava25Support() {
        return JAVA_25_COMPATIBLE;
    }

    public static int getVersion() {
        return VERSION;
    }

    public static boolean supportsVersion(int ver) {
        if (ver > VERSION) return false;
        if (ver >= 25) return JAVA_25_COMPATIBLE;
        if (ver >= 17) return JAVA_17_COMPATIBLE;
        if (ver == 9) return JAVA_9_COMPATIBLE;
        if (ver >= 8) return true;
        throw new IllegalArgumentException("Unknown Java version: " + ver);
    }
}
