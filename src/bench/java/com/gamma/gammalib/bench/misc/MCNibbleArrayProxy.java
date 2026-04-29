package com.gamma.gammalib.bench.misc;

import com.gamma.gammalib.multi.nibblearray.FastAtomicNibbleArray;

public class MCNibbleArrayProxy implements FastAtomicNibbleArray {

    public byte[] data;
    public final int depthBits;
    public final int depthBitsPlusFour;

    public MCNibbleArrayProxy(int p_i1992_1_) {
        this.data = new byte[p_i1992_1_ >> 1];
        this.depthBits = 4;
        this.depthBitsPlusFour = 8;
    }

    public MCNibbleArrayProxy(int p_i1992_1_, int p_i1992_2_) {
        this.data = new byte[p_i1992_1_ >> 1];
        this.depthBits = p_i1992_2_;
        this.depthBitsPlusFour = p_i1992_2_ + 4;
    }

    public MCNibbleArrayProxy(byte[] p_i1993_1_, int p_i1993_2_) {
        this.data = p_i1993_1_;
        this.depthBits = p_i1993_2_;
        this.depthBitsPlusFour = p_i1993_2_ + 4;
    }

    @Override
    public int get(int idx) {
        int i1 = idx >> 1;
        int j1 = idx & 1;
        synchronized (this) {
            return j1 == 0 ? this.data[i1] & 15 : this.data[i1] >> 4 & 15;
        }
    }

    public synchronized int get(int p_76582_1_, int p_76582_2_, int p_76582_3_) {
        int l = p_76582_2_ << this.depthBitsPlusFour | p_76582_3_ << this.depthBits | p_76582_1_;
        return this.get(l);
    }

    @Override
    public void set(int idx, int value) {
        int j1 = idx >> 1;
        int k1 = idx & 1;
        synchronized (this) {
            if (k1 == 0) {
                this.data[j1] = (byte) (this.data[j1] & 240 | value & 15);
            } else {
                this.data[j1] = (byte) (this.data[j1] & 15 | (value & 15) << 4);
            }
        }
    }

    public synchronized void set(int p_76581_1_, int p_76581_2_, int p_76581_3_, int p_76581_4_) {
        int i1 = p_76581_2_ << this.depthBitsPlusFour | p_76581_3_ << this.depthBits | p_76581_1_;
        this.set(i1, p_76581_4_);
    }

    @Override
    public synchronized int incrementAndGet(int idx) {
        int newValue = (this.get(idx) + 1) & 0xF;
        this.set(idx, newValue);
        return newValue;
    }

    @Override
    public byte[] getByteArray() {
        return this.data;
    }

    @Override
    public boolean compareAndSet(int i, int expect, int newValue) {
        synchronized (this) {
            if (this.get(i) == expect) {
                this.set(i, newValue);
                return true;
            }
            return false;
        }
    }
}
