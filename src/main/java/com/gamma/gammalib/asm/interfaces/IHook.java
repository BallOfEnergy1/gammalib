package com.gamma.gammalib.asm.interfaces;

public interface IHook {

    /**
     * Called when a class is about to be transformed.
     *
     * @return true if the transformation should continue, false to cancel.
     */
    boolean beginTransform(String name, String transformedName, byte[] basicClass);
}
