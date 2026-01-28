package com.gamma.gammalib.asm;

import javax.annotation.Nonnull;

import com.gamma.gammalib.asm.interfaces.ICheckTransformer;
import com.gamma.gammalib.asm.interfaces.IHook;
import com.gamma.gammalib.asm.interfaces.ITransformer;
import com.gamma.gammalib.core.GammaLibLogger;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;

public class ASMRegistry {

    static final ObjectArrayList<Entry> TRANSFORMERS = new ObjectArrayList<>();
    static final ObjectArrayList<Entry> CHECK_TRANSFORMERS = new ObjectArrayList<>();
    static final ObjectArrayList<IHook> HOOKS = new ObjectArrayList<>();

    public static void register(String modid, ITransformer transformer) {
        TRANSFORMERS.add(new Entry(modid, transformer));
    }

    public static void registerCheck(String modid, ICheckTransformer transformer) {
        CHECK_TRANSFORMERS.add(new Entry(modid, transformer));
    }

    public static void registerHook(IHook hook) {
        HOOKS.add(hook);
    }

    static boolean beginTransform(String name, String transformedName, byte[] basicClass) {
        boolean shouldContinue = false;
        for (IHook hook : HOOKS) {
            try {
                shouldContinue = hook.beginTransform(name, transformedName, basicClass);
            } catch (Throwable t) {
                GammaLibLogger.error(
                    "Failed to run hook {}",
                    hook.getClass()
                        .getSimpleName());
                GammaLibLogger.warn("Suppressing hook, it will no longer be processed.");
                HOOKS.remove(hook);
                t.printStackTrace();
            }
            if (shouldContinue) break;
        }
        return shouldContinue;
    }

    record Entry(String modid, ITransformer transformer) {

        public ICheckTransformer checkTransformer() {
            return (ICheckTransformer) transformer;
        }

        @Override
        @Nonnull
        public String toString() {
            return transformer.getClass()
                .getSimpleName() + " from mod "
                + modid;
        }
    }
}
