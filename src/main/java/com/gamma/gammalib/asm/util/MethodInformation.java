package com.gamma.gammalib.asm.util;

import org.spongepowered.asm.lib.tree.ClassNode;
import org.spongepowered.asm.lib.tree.LocalVariableNode;
import org.spongepowered.asm.lib.tree.MethodNode;

import com.github.bsideup.jabel.Desugar;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;

@Desugar
public record MethodInformation(MethodNode methodNode, ClassNode classNode,
    Int2ObjectMap<LocalVariableNode> localsMap) {}
