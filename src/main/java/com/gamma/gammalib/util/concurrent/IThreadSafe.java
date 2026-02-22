package com.gamma.gammalib.util.concurrent;

import com.gamma.gammalib.api.concurrent.SpoolThreadSafe;

public interface IThreadSafe {

    static boolean isConcurrent(Object obj) {
        return obj instanceof IThreadSafe || obj.getClass().isAnnotationPresent(SpoolThreadSafe.class);
    }
}
