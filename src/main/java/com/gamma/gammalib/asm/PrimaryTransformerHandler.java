package com.gamma.gammalib.asm;

import java.util.Arrays;

import net.minecraft.launchwrapper.IClassTransformer;

import org.spongepowered.asm.lib.ClassReader;
import org.spongepowered.asm.lib.ClassWriter;
import org.spongepowered.asm.lib.tree.ClassNode;
import org.spongepowered.asm.lib.tree.MethodNode;
import org.spongepowered.asm.lib.util.CheckClassAdapter;

import com.gamma.gammalib.asm.interfaces.IConstructorTransformer;
import com.gamma.gammalib.asm.interfaces.IFieldTransformer;
import com.gamma.gammalib.asm.interfaces.ISuperclassTransformer;
import com.gamma.gammalib.asm.interfaces.ITransformer;
import com.gamma.gammalib.asm.util.ClassHierarchyUtil;

import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import it.unimi.dsi.fastutil.objects.ObjectSet;

@SuppressWarnings("unused")
public class PrimaryTransformerHandler implements IClassTransformer {

    private static final ObjectSet<String> processedClasses = new ObjectOpenHashSet<>();

    @Override
    public byte[] transform(String name, String transformedName, byte[] basicClass) {

        if (ASMRegistry.TRANSFORMERS.isEmpty() && ASMRegistry.CHECK_TRANSFORMERS.isEmpty()) {
            processedClasses.add(transformedName);
            return basicClass;
        }

        if (processedClasses.contains(transformedName)) return basicClass;
        if (basicClass == null) {
            processedClasses.add(transformedName);
            return null;
        }

        boolean transform = ASMRegistry.beginTransform(name, transformedName, basicClass);
        if (!transform) {
            processedClasses.add(transformedName);
            return basicClass;
        }

        if (transformedName.contains("it.unimi.dsi.fastutil")
            || transformedName.contains("com.gtnewhorizon.gtnhlib.asm")) {
            processedClasses.add(transformedName);
            return basicClass;
        }

        String className = transformedName.replace(".", "/");

        final ClassReader classReader = new ClassReader(basicClass);
        final ClassNode classNode = new ClassNode();
        classReader.accept(classNode, ClassReader.EXPAND_FRAMES);

        boolean changed = false;

        changed |= transform(transformedName, classNode, basicClass);
        changed |= runChecks(transformedName, classNode, basicClass);

        if (changed) {
            ClassWriter cw = new SafeClassWriter(classReader, ClassWriter.COMPUTE_FRAMES | ClassWriter.COMPUTE_MAXS);
            classNode.accept(cw);

            final byte[] bytes;

            bytes = cw.toByteArray();

            ClassReader checker = new ClassReader(bytes);
            checker.accept(new CheckClassAdapter(new ClassNode()), 0);
            processedClasses.add(transformedName);
            return bytes;
        }
        processedClasses.add(transformedName);
        return basicClass;
    }

    public boolean transform(String transformedName, ClassNode classNode, byte[] bytecode) {

        // Get all valid transformers (transformers that would like to transform this class).
        ASMRegistry.Entry[] validEntries = new ASMRegistry.Entry[ASMRegistry.TRANSFORMERS.size()];
        int i = 0;
        for (ASMRegistry.Entry entry : ASMRegistry.TRANSFORMERS) {
            if (!entry.transformer()
                .getTargetClasses()
                .find(bytecode, true)) continue;
            validEntries[i] = entry;
            i++;
        }

        if (i == 0) return false;

        // Trim valid transformers array to size.
        System.arraycopy(validEntries, 0, validEntries = new ASMRegistry.Entry[i], 0, i);

        boolean changed = false;

        for (ASMRegistry.Entry validEntry : validEntries) {
            ITransformer validTransformer = validEntry.transformer();
            if (validTransformer instanceof ISuperclassTransformer)
                changed |= ((ISuperclassTransformer) validTransformer).transformSuperclass(transformedName, classNode);

            boolean isFieldTransformationAllowed = false;
            if (validTransformer instanceof IFieldTransformer) {
                String[] excludedNodes = ((IFieldTransformer) validTransformer).getExcludedClassNodes();
                if (!Arrays.asList(excludedNodes)
                    .contains(classNode.name)) {
                    isFieldTransformationAllowed = true;
                }
            }

            for (MethodNode mn : classNode.methods) {
                if (validTransformer instanceof IConstructorTransformer) {
                    boolean[] results = ((IConstructorTransformer) validTransformer)
                        .transformConstructors(transformedName, mn);
                    changed |= results[0];
                    if (results[1]) {
                        throw new IllegalStateException(
                            "Failed to transform " + transformedName + " due to missing constructor call");
                    }
                }
                if (isFieldTransformationAllowed) {
                    changed |= ((IFieldTransformer) validTransformer).transformFieldAccesses(transformedName, mn);
                }
            }
        }

        return changed;
    }

    public boolean runChecks(String transformedName, ClassNode classNode, byte[] bytecode) {
        boolean changed = false;

        ASMRegistry.Entry currentEntry = null;
        try {
            for (ASMRegistry.Entry checkTransformer : ASMRegistry.CHECK_TRANSFORMERS) {
                currentEntry = checkTransformer;
                changed |= checkTransformer.checkTransformer()
                    .performCheck(transformedName, classNode, bytecode);
            }
        } catch (Exception e) {
            throw new IllegalStateException(
                "Transformer " + currentEntry.toString() + " failed while performing checks via ASM.",
                e);
        }
        // Add more checks here.

        return changed;
    }

    private static class SafeClassWriter extends ClassWriter {

        public SafeClassWriter(ClassReader classReader, int flags) {
            super(classReader, flags);
        }

        public SafeClassWriter(int flags) {
            super(flags);
        }

        @Override
        protected String getCommonSuperClass(String type1, String type2) {
            return ClassHierarchyUtil.getInstance()
                .getCommonSuperClass(type1, type2);
        }
    }
}
