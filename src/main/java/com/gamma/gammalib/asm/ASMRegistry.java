package com.gamma.gammalib.asm;

import javax.annotation.Nonnull;

import com.gamma.gammalib.asm.interfaces.ICheckTransformer;
import com.gamma.gammalib.asm.interfaces.ITransformer;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;

public class ASMRegistry {

    static final ObjectArrayList<Entry> TRANSFORMERS = new ObjectArrayList<>();
    static final ObjectArrayList<Entry> CHECK_TRANSFORMERS = new ObjectArrayList<>();

    public static void register(String modid, ITransformer transformer) {
        TRANSFORMERS.add(new Entry(modid, transformer));
    }

    public static void registerCheck(String modid, ICheckTransformer transformer) {
        CHECK_TRANSFORMERS.add(new Entry(modid, transformer));
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
