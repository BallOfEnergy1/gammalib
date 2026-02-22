package com.gamma.gammalib.api.concurrent;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * This annotation can be used on external classes if they are
 * <i>intended to be thread-safe</i>.
 * <p>
 * Mods are allowed to detect this annotation and change its
 * execution process as needed to increase performance with
 * other mods.
 * </p>
 * <p>
 * This is NOT required. If this annotation is not present,
 * mods should assume that the class is not thread-safe.
 * </p>
 * <p>
 * It is up to the implementing class to decide the locking
 * architecture to use. As long as their methods can be executed
 * concurrently with each other without issues, it can implement
 * this interface.
 * </p>
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface SpoolThreadSafe {
    // Not really much of anything here...
    // I'll put some utility functions here eventually (maybe...)
}
