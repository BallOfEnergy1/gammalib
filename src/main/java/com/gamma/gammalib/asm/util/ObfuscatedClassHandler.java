package com.gamma.gammalib.asm.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import com.gamma.gammalib.core.GammaLibLogger;
import com.google.common.collect.ImmutableMap;

public class ObfuscatedClassHandler {

    private static final ImmutableMap<String, String> obfuscatedToDeobfuscatedNameMap;
    private static final ImmutableMap<String, String> deobfuscatedToObfuscatedNameMap;

    static {
        ImmutableMap.Builder<String, String> obfToDeobfBuilder = ImmutableMap.builder();
        ImmutableMap.Builder<String, String> deobfToObfBuilder = ImmutableMap.builder();

        InputStream is = Thread.currentThread()
            .getContextClassLoader()
            .getResourceAsStream("META-INF/class_mappings.srg");
        if (is == null) {
            throw new IllegalStateException("Could not load class_mappings.srg, unable to launch.");
        }
        try (BufferedReader br = new BufferedReader(new InputStreamReader(is))) {
            br.lines()
                .forEach(line -> {
                    String[] entries = line.split(" ");
                    String obfuscated = entries[0].intern();
                    String deobfuscated = entries[1].intern();
                    obfToDeobfBuilder.put(obfuscated, deobfuscated);
                    deobfToObfBuilder.put(deobfuscated, obfuscated);
                });
        } catch (IOException e) {
            throw new IllegalStateException("Failed to read class_mappings.srg", e);
        }

        obfuscatedToDeobfuscatedNameMap = obfToDeobfBuilder.build();
        deobfuscatedToObfuscatedNameMap = deobfToObfBuilder.build();

        GammaLibLogger.info("Successfully loaded " + obfuscatedToDeobfuscatedNameMap.size() + " class names.");
    }

    public static String mapDeobfuscatedClass(String deobfuscatedName) {
        String out;
        String name = binaryToInternal(deobfuscatedName);
        GammaLibLogger
            .debug("Mapping classname: " + name + ", result: " + (out = deobfuscatedToObfuscatedNameMap.get(name)));
        return out;
    }

    public static String mapObfuscatedClass(String obfuscatedName) {
        String out;
        GammaLibLogger.debug(
            "Mapping classname: " + obfuscatedName
                + ", result: "
                + (out = obfuscatedToDeobfuscatedNameMap.get(obfuscatedName)));
        return out;
    }

    private static String binaryToInternal(String binary) {
        return binary.replace('.', '/');
    }
}
