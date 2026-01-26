package com.gamma.gammalib.util.concurrent;

import com.gamma.gammalib.api.concurrent.IExternalThreadSafe;

public interface IThreadSafe {

    static boolean isConcurrent(Object obj) {
        return obj instanceof IThreadSafe || obj instanceof IExternalThreadSafe;
    }
}
