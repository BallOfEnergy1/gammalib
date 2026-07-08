package com.gamma.gammalib.asm.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

import org.spongepowered.asm.obfuscation.mapping.common.MappingMethod;
import org.spongepowered.asm.obfuscation.mapping.mcp.MappingFieldSrg;

import com.gamma.gammalib.core.GammaLibLogger;
import com.google.common.collect.ImmutableBiMap;

import de.siegmar.fastcsv.reader.CsvReader;
import de.siegmar.fastcsv.reader.NamedCsvRecord;

public class ObfuscatedClassHandler {

    private static final ImmutableBiMap<String, String> classMap;
    private static final ImmutableBiMap<MappingFieldSrg, MappingFieldSrg> fieldMap;
    private static final ImmutableBiMap<MappingMethod, MappingMethod> methodMap;

    private static final ImmutableBiMap<MappingFieldSrg, MappingFieldSrg> fieldMapMCP;
    private static final ImmutableBiMap<MappingMethod, MappingMethod> methodMapMCP;

    static {
        ImmutableBiMap.Builder<String, String> classMapBuilder = ImmutableBiMap.builder();

        ImmutableBiMap.Builder<MappingFieldSrg, MappingFieldSrg> fieldMapBuilder = ImmutableBiMap.builder();
        ImmutableBiMap.Builder<MappingMethod, MappingMethod> methodMapBuilder = ImmutableBiMap.builder();

        Map<String, String> srgFieldToOwnerClassMap = new HashMap<>();
        Map<String, MappingMethod> srgMethodToOwnerClassAndDescMap = new HashMap<>();

        InputStream is = Thread.currentThread()
            .getContextClassLoader()
            .getResourceAsStream("META-INF/joined.srg");
        if (is == null) {
            throw new IllegalStateException("Could not load joined.srg, unable to launch.");
        }

        try (BufferedReader br = new BufferedReader(new InputStreamReader(is))) {
            br.lines()
                .forEach(line -> {
                    if (line == null || line.isEmpty() || line.startsWith("#")) {
                        return;
                    }

                    String type = line.substring(0, 2);
                    String[] args = line.substring(4)
                        .split(" ");

                    switch (type) {
                        case "CL" -> classMapBuilder.put(args[0], args[1]);
                        case "FD" -> {
                            MappingFieldSrg obf = new MappingFieldSrg(args[0]);
                            MappingFieldSrg srg = new MappingFieldSrg(args[1]);
                            fieldMapBuilder.put(obf, srg);
                            srgFieldToOwnerClassMap.put(srg.getSimpleName(), srg.getOwner());
                        }
                        case "MD" -> {
                            MappingMethod obf = new MappingMethod(args[0], args[1]);
                            MappingMethod srg = new MappingMethod(args[2], args[3]);
                            methodMapBuilder.put(obf, srg);
                            srgMethodToOwnerClassAndDescMap.put(srg.getSimpleName(), srg);
                        }
                    }
                });
        } catch (IOException e) {
            throw new IllegalStateException("Failed to read class_mappings.srg", e);
        }

        classMap = classMapBuilder.build();
        fieldMap = fieldMapBuilder.build();
        methodMap = methodMapBuilder.build();

        GammaLibLogger.info(
            "Successfully loaded " + classMap
                .size() + " class, " + fieldMap.size() + " field, and " + methodMap.size() + " method mappings (SRG).");

        ImmutableBiMap.Builder<MappingFieldSrg, MappingFieldSrg> fieldMapBuilderMCP = ImmutableBiMap.builder();
        ImmutableBiMap.Builder<MappingMethod, MappingMethod> methodMapBuilderMCP = ImmutableBiMap.builder();

        is = Thread.currentThread()
            .getContextClassLoader()
            .getResourceAsStream("META-INF/fields.csv");
        if (is == null) {
            throw new IllegalStateException("Could not load fields.csv, unable to launch.");
        }

        try (CsvReader<NamedCsvRecord> reader = CsvReader.builder()
            .ofNamedCsvRecord(is)) {
            for (NamedCsvRecord record : reader) {
                String searge = record.getField("searge");
                String name = record.getField("name");

                fieldMap.get(new MappingFieldSrg(searge));
                String owner = srgFieldToOwnerClassMap.get(searge);
                MappingFieldSrg fieldSRG = new MappingFieldSrg(owner + "/" + searge);
                MappingFieldSrg fieldMCP = new MappingFieldSrg(owner + "/" + name);
                fieldMapBuilderMCP.put(fieldSRG, fieldMCP);
            }
        } catch (IOException e) {
            throw new IllegalStateException("Failed to read fields.csv", e);
        }

        fieldMapMCP = fieldMapBuilderMCP.build();

        is = Thread.currentThread()
            .getContextClassLoader()
            .getResourceAsStream("META-INF/methods.csv");
        if (is == null) {
            throw new IllegalStateException("Could not load methods.csv, unable to launch.");
        }

        try (CsvReader<NamedCsvRecord> reader = CsvReader.builder()
            .ofNamedCsvRecord(is)) {
            for (NamedCsvRecord record : reader) {
                String searge = record.getField("searge");
                String name = record.getField("name");

                fieldMap.get(new MappingFieldSrg(searge));
                MappingMethod owner = srgMethodToOwnerClassAndDescMap.get(searge);
                MappingMethod methodSRG = new MappingMethod(owner.getOwner(), searge, owner.getDesc());
                MappingMethod methodMCP = new MappingMethod(owner.getOwner(), name, owner.getDesc());
                methodMapBuilderMCP.put(methodSRG, methodMCP);
            }
        } catch (IOException e) {
            throw new IllegalStateException("Failed to read methods.csv", e);
        }

        methodMapMCP = methodMapBuilderMCP.build();

        GammaLibLogger.info(
            "Successfully loaded " + fieldMapMCP.size()
                + " field, and "
                + methodMapMCP.size()
                + " method mappings (MCP).");
    }

    public static String classSRGToObf(String deobfuscatedName) {
        String out;
        String name = binaryToInternal(deobfuscatedName);
        GammaLibLogger.debug(
            "Mapping classname: " + name
                + ", result: "
                + (out = classMap.inverse()
                    .get(name)));
        return out;
    }

    public static String classObfToSRG(String obfuscatedName) {
        String out;
        GammaLibLogger
            .debug("Mapping classname: " + obfuscatedName + ", result: " + (out = classMap.get(obfuscatedName)));
        return out;
    }

    public static MappingFieldSrg fieldSRGToObf(String owner, String deobfuscatedName) {
        String name = binaryToInternal(owner) + "/" + deobfuscatedName;
        MappingFieldSrg field = new MappingFieldSrg(name);
        MappingFieldSrg out = fieldMap.inverse()
            .get(field);
        GammaLibLogger
            .debug("Mapping field using SRG: " + name + ", result: " + (out == null ? null : out.serialise()));
        return out;
    }

    public static MappingFieldSrg fieldObfToSRG(String owner, String obfuscatedName) {
        String name = owner + "/" + obfuscatedName;
        MappingFieldSrg field = new MappingFieldSrg(name);
        MappingFieldSrg out = fieldMap.get(field);
        GammaLibLogger
            .debug("Mapping field using SRG: " + name + ", result: " + (out == null ? null : out.serialise()));
        return out;
    }

    public static MappingFieldSrg fieldMCPToObf(String owner, String deobfuscatedName) {
        MappingFieldSrg out;
        String name = binaryToInternal(owner) + "/" + deobfuscatedName;
        MappingFieldSrg field = new MappingFieldSrg(name);
        out = fieldMapMCP.inverse()
            .get(field);
        if (out != null) {
            GammaLibLogger.debug("Mapping field using MCP: " + name + ", result: " + out.serialise());
            field = out;
            name = out.serialise();
        }
        GammaLibLogger.debug(
            "Mapping field using SRG: " + name
                + ", result: "
                + (out = fieldMap.inverse()
                    .get(field)).serialise());
        return out;
    }

    public static MappingFieldSrg fieldObfToMCP(String owner, String obfuscatedName) {
        MappingFieldSrg out;
        String name = owner + "/" + obfuscatedName;
        MappingFieldSrg field = new MappingFieldSrg(name);
        out = fieldMap.get(field);
        if (out != null) {
            GammaLibLogger.debug("Mapping field using SRG: " + name + ", result: " + out.serialise());
            field = out;
            name = out.serialise();
        }
        GammaLibLogger
            .debug("Mapping field using MCP: " + name + ", result: " + (out = fieldMapMCP.get(field)).serialise());
        return out;
    }

    public static MappingFieldSrg fieldMCPToSRG(String owner, String mcpName) {
        String name = owner + "/" + mcpName;
        MappingFieldSrg out = fieldMapMCP.inverse()
            .get(new MappingFieldSrg(name));
        if (out != null) {
            GammaLibLogger.debug("Mapping field using MCP: " + name + ", result: " + out.serialise());
        }
        return out;
    }

    public static MappingFieldSrg fieldSRGToMCP(String owner, String srgName) {
        String name = owner + "/" + srgName;
        MappingFieldSrg out = fieldMapMCP.get(new MappingFieldSrg(name));
        if (out != null) {
            GammaLibLogger.debug("Mapping field using MCP: " + name + ", result: " + out.serialise());
        }
        return out;
    }

    public static MappingMethod methodSRGToObf(String fullyQualifiedDeobfuscatedName, String desc) {
        MappingMethod out;
        String name = binaryToInternal(fullyQualifiedDeobfuscatedName);
        MappingMethod target = new MappingMethod(fullyQualifiedDeobfuscatedName, desc);
        GammaLibLogger.debug(
            "Mapping method using SRG: " + name
                + ", result: "
                + (out = methodMap.inverse()
                    .get(target)).serialise());
        return out;
    }

    public static MappingMethod methodObfToSRG(String fullyQualifiedObfuscatedName, String desc) {
        MappingMethod out;
        MappingMethod target = new MappingMethod(fullyQualifiedObfuscatedName, desc);
        GammaLibLogger.debug(
            "Mapping method using SRG: " + fullyQualifiedObfuscatedName
                + ", result: "
                + (out = methodMap.get(target)).serialise());
        return out;
    }

    public static MappingMethod methodMCPToObf(String fullyQualifiedDeobfuscatedName, String desc) {
        MappingMethod out;
        String name = binaryToInternal(fullyQualifiedDeobfuscatedName);
        MappingMethod target = new MappingMethod(fullyQualifiedDeobfuscatedName, desc);
        out = methodMapMCP.inverse()
            .get(target);
        if (out != null) {
            GammaLibLogger.debug("Mapping method using MCP: " + name + ", result: " + out.serialise());
            target = out;
            name = out.serialise();
        }
        GammaLibLogger.debug(
            "Mapping method using SRG: " + name
                + ", result: "
                + (out = methodMap.inverse()
                    .get(target)).serialise());
        return out;
    }

    public static MappingMethod methodObfToMCP(String fullyQualifiedObfuscatedName, String desc) {
        MappingMethod out;
        MappingMethod target = new MappingMethod(fullyQualifiedObfuscatedName, desc);
        out = methodMap.get(target);
        if (out != null) {
            GammaLibLogger
                .debug("Mapping method using SRG: " + fullyQualifiedObfuscatedName + ", result: " + out.serialise());
            target = out;
            fullyQualifiedObfuscatedName = out.serialise();
        }
        GammaLibLogger.debug(
            "Mapping method using MCP: " + fullyQualifiedObfuscatedName
                + ", result: "
                + (out = methodMapMCP.get(target)).serialise());
        return out;
    }

    public static MappingMethod methodMCPToSRG(String fullyQualifiedMCPName, String desc) {
        MappingMethod out = methodMapMCP.inverse()
            .get(new MappingMethod(fullyQualifiedMCPName, desc));
        if (out != null) {
            GammaLibLogger.debug("Mapping field using MCP: " + fullyQualifiedMCPName + ", result: " + out.serialise());
        }
        return out;
    }

    public static MappingMethod methodSRGToMCP(String fullyQualifiedSRGName, String desc) {
        MappingMethod out = methodMapMCP.get(new MappingMethod(fullyQualifiedSRGName, desc));
        if (out != null) {
            GammaLibLogger.debug("Mapping field using MCP: " + fullyQualifiedSRGName + ", result: " + out.serialise());
        }
        return out;
    }

    private static String binaryToInternal(String binary) {
        return binary.replace('.', '/');
    }
}
