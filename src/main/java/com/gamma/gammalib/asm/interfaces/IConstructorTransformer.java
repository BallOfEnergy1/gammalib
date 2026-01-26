package com.gamma.gammalib.asm.interfaces;

import org.spongepowered.asm.lib.tree.MethodNode;

public interface IConstructorTransformer extends ITransformer {

    /**
     * Transforms constructors in a certain method node.
     *
     * @return Was the class changed, `init`.
     */
    boolean[] transformConstructors(String transformedName, MethodNode mn);
}
