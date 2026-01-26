package com.gamma.gammalib.util.concurrent;

public interface ILockUser extends IThreadSafe {

    void readLock();

    void readUnlock();

    void writeLock();

    void writeUnlock();
}
