package com.gamma.gammalib.asm.util;

import org.spongepowered.asm.lib.tree.AbstractInsnNode;

import com.github.bsideup.jabel.Desugar;

@Desugar
public record IteratorDescriptor(StackRebuilder rebuilder, MethodInformation methodInfo, AbstractInsnNode start,
    AbstractInsnNode comparisonNode, AbstractInsnNode end) {}
