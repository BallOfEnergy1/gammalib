package com.gamma.gammalib.asm.interfaces;

import org.spongepowered.asm.lib.tree.MethodNode;

public interface IMethodTransformer extends ITransformer {

    /**
     * Transforms the contents of a certain method node.
     *
     * @return Was the class changed?
     */
    boolean transformMethodContents(String transformedName, MethodNode mn);
}
