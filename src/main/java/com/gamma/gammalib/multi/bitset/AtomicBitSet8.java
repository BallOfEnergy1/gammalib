package com.gamma.gammalib.multi.bitset;

import java.util.concurrent.atomic.AtomicIntegerFieldUpdater;

public class AtomicBitSet8 implements FastAtomicBitSet {

    private static final class Cell {

        volatile int value; // 1 = true, 0 = false
    }

    private static final AtomicIntegerFieldUpdater<Cell> UPDATER = AtomicIntegerFieldUpdater
        .newUpdater(Cell.class, "value");

    private final Cell[] cells;

    private final int length;

    public AtomicBitSet8(int bitsLength) {
        cells = new Cell[length = bitsLength];
        for (int i = 0; i < bitsLength; i++) {
            cells[i] = new Cell();
        }
    }

    @Override
    public void set(int bitIndex) {
        Cell c = cells[bitIndex];
        int prev;
        do {
            prev = c.value;
            if (prev == 1) return;
        } while (!UPDATER.compareAndSet(c, 0, 1));
    }

    @Override
    public void clear(int bitIndex) {
        Cell c = cells[bitIndex];
        int prev;
        do {
            prev = c.value;
            if (prev == 0) return;
        } while (!UPDATER.compareAndSet(c, 1, 0));
    }

    @Override
    public void set(int bitIndex, boolean value) {
        if (value) set(bitIndex);
        else clear(bitIndex);
    }

    @Override
    public void flip(int bitIndex) {
        Cell c = cells[bitIndex];
        int prev;
        do {
            prev = c.value;
        } while (!UPDATER.compareAndSet(c, prev, prev ^ 1));
    }

    @Override
    public boolean get(int bitIndex) {
        return cells[bitIndex].value != 0;
    }

    @Override
    public int length() {
        return length;
    }
}
