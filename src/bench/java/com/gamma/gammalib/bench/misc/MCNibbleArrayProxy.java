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

    public synchronized int get(int p_76582_1_, int p_76582_2_, int p_76582_3_) {
        int l = p_76582_2_ << this.depthBitsPlusFour | p_76582_3_ << this.depthBits | p_76582_1_;
        int i1 = l >> 1;
        int j1 = l & 1;
        return j1 == 0 ? this.data[i1] & 15 : this.data[i1] >> 4 & 15;
    }

    @Override
    public int get(int idx) {
        return this.get(idx >> 8, idx >> 4 & 15, idx & 15);
    }

    public synchronized void set(int p_76581_1_, int p_76581_2_, int p_76581_3_, int p_76581_4_) {
        int i1 = p_76581_2_ << this.depthBitsPlusFour | p_76581_3_ << this.depthBits | p_76581_1_;
        int j1 = i1 >> 1;
        int k1 = i1 & 1;

        if (k1 == 0) {
            this.data[j1] = (byte) (this.data[j1] & 240 | p_76581_4_ & 15);
        } else {
            this.data[j1] = (byte) (this.data[j1] & 15 | (p_76581_4_ & 15) << 4);
        }
    }

    @Override
    public void set(int idx, int value) {
        this.set(idx >> 8, idx >> 4 & 15, idx & 15, value);
    }

    @Override
    public synchronized int incrementAndGet(int idx) {
        int newValue = this.get(idx) + 1;
        this.set(idx, newValue);
        return newValue;
    }
}
