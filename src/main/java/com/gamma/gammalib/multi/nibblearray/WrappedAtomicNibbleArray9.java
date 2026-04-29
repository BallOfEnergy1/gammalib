package com.gamma.gammalib.multi.nibblearray;

import java.lang.invoke.MethodHandles;
import java.lang.invoke.VarHandle;

import com.google.common.annotations.VisibleForTesting;

public class WrappedAtomicNibbleArray9 implements FastAtomicNibbleArray {

    private static final VarHandle BYTE_ARRAY_HANDLE = MethodHandles.arrayElementVarHandle(byte[].class);

    private final byte[] array;

    public WrappedAtomicNibbleArray9(final int length) {
        this.array = new byte[(length + 1) >>> 1];
    }

    public WrappedAtomicNibbleArray9(final byte[] array) {
        this.array = array;
    }

    @Override
    public void set(final int i, final int newValue) {
        final byte[] array = this.array;
        final int byteIdx = i >>> 1;
        final int shift = (i & 1) << 2;
        final int mask = 0xF << shift;
        final int valueToInject = (newValue & 0xF) << shift;

        for (;;) {
            byte oldByte = (byte) BYTE_ARRAY_HANDLE.getVolatile(array, byteIdx);
            byte newByte = (byte) ((oldByte & ~mask) | valueToInject);
            if (BYTE_ARRAY_HANDLE.compareAndSet(array, byteIdx, oldByte, newByte)) break;
            Thread.onSpinWait();
        }
    }

    @Override
    public int get(final int i) {
        final byte[] array = this.array;
        final int byteIdx = i >>> 1;
        final int shift = (i & 1) << 2;
        byte b = (byte) BYTE_ARRAY_HANDLE.get(array, byteIdx);
        return (b >>> shift) & 0xF;
    }

    @Override
    public boolean compareAndSet(int i, int expect, int newValue) {
        final byte[] array = this.array;
        final int byteIdx = i >>> 1;
        final int shift = (i & 1) << 2;
        final int mask = 0xF << shift;
        final int expected = (expect & 0xF) << shift;
        final int valueToInject = (newValue & 0xF) << shift;

        for (;;) {
            byte oldByte = (byte) BYTE_ARRAY_HANDLE.getVolatile(array, byteIdx);
            if ((oldByte & mask) != expected) return false;
            byte newByte = (byte) ((oldByte & ~mask) | valueToInject);
            if (BYTE_ARRAY_HANDLE.compareAndSet(array, byteIdx, oldByte, newByte)) break;
            Thread.onSpinWait();
        }
        return true;
    }

    @Override
    @VisibleForTesting
    public int incrementAndGet(final int i) {
        final byte[] array = this.array;
        final int byteIdx = i >>> 1;
        final int shift = (i & 1) << 2;
        final int mask = 0xF << shift;

        for (;;) {
            byte oldByte = (byte) BYTE_ARRAY_HANDLE.getVolatile(array, byteIdx);
            int oldNibble = (oldByte >>> shift) & 0xF;
            int newNibble = (oldNibble + 1) & 0xF;
            byte newByte = (byte) ((oldByte & ~mask) | (newNibble << shift));
            if (BYTE_ARRAY_HANDLE.compareAndSet(array, byteIdx, oldByte, newByte)) return newNibble;
            Thread.onSpinWait();
        }
    }

    @Override
    public byte[] getByteArray() {
        return array;
    }
}
