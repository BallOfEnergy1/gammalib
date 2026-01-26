package com.gamma.gammalib.asm.interfaces;

import org.spongepowered.asm.lib.tree.ClassNode;

public interface ISuperclassTransformer {

    /**
     * Transforms the superclass for a certain class node.
     *
     * @return Was the class changed?
     */
    boolean transformSuperclass(String transformedName, ClassNode cn);
}
