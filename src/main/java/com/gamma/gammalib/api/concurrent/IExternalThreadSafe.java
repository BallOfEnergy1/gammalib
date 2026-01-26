package com.gamma.gammalib.api.concurrent;

/**
 * This interface can be implemented in external classes if they are
 * <i>intended to be thread-safe</i>. This is the API equivalent of
 * {@link com.gamma.gammalib.util.concurrent.IThreadSafe}.
 * <p>
 * Mods are allowed to detect implementors of this interface and
 * change its execution process as needed to increase performance
 * with other mods.
 * </p>
 * <p>
 * This won't always be needed, though there are a few special
 * situations where it is very performant to do so.
 * </p>
 * <p>
 * It is up to the implementing class to decide the locking
 * mechanism to use (or a lock-free architecture). As long as
 * their methods can be executed concurrently with each other
 * without issues, it can implement this interface.
 * </p>
 */
public interface IExternalThreadSafe {
    // Not really much of anything here...
    // I'll put some utility functions here eventually (maybe...)
}
