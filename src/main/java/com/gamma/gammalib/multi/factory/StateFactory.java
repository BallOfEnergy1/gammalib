package com.gamma.gammalib.multi.factory;

import java.util.function.Supplier;

import com.gamma.gammalib.config.ImplConfig;
import com.gamma.gammalib.multi.MultiJavaUtil;
import com.gamma.gammalib.multi.state.FastThreadLocalState;
import com.gamma.gammalib.multi.state.ThreadLocalState25;
import com.gamma.gammalib.multi.state.ThreadLocalState8;

public class StateFactory {

    public static <T> FastThreadLocalState<T> create(Supplier<T> supplier, boolean intentionalLeak) {
        if (!intentionalLeak && MultiJavaUtil.hasJava25Support() && ImplConfig.useJava25Features) {
            return new ThreadLocalState25<>(supplier);
        }
        return new ThreadLocalState8<>(supplier, intentionalLeak);
    }
}
