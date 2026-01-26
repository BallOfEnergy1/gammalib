package com.gamma.gammalib.multi.factory;

import com.gamma.gammalib.config.ImplConfig;
import com.gamma.gammalib.multi.MultiJavaUtil;
import com.gamma.gammalib.multi.bitset.AtomicBitSet8;
import com.gamma.gammalib.multi.bitset.AtomicBitSet9;
import com.gamma.gammalib.multi.bitset.AtomicBitSetCompact;
import com.gamma.gammalib.multi.bitset.FastAtomicBitSet;

public class BitSetFactory {

    public static FastAtomicBitSet create(int size) {
        if (MultiJavaUtil.hasJava9Support() && ImplConfig.useJava9Features) {
            return new AtomicBitSet9(size);
        }
        if (ImplConfig.useCompactImpls) {
            return new AtomicBitSetCompact(size);
        }
        return new AtomicBitSet8(size);
    }
}
