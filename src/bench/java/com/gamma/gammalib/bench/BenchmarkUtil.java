package com.gamma.gammalib.bench;

public final class BenchmarkUtil {

    private BenchmarkUtil() {}

    public static int[] generateIndices(int size, int count, long seed) {
        int[] values = new int[count];
        long state = seed;
        for (int i = 0; i < count; i++) {
            state = mix(state);
            values[i] = (int) ((state >>> 1) % size);
        }
        return values;
    }

    public static byte[] generateBytes(int count, long seed) {
        byte[] values = new byte[count];
        long state = seed;
        for (int i = 0; i < count; i++) {
            state = mix(state);
            values[i] = (byte) state;
        }
        return values;
    }

    private static long mix(long state) {
        state ^= state >>> 33;
        state *= 0xff51afd7ed558ccdL;
        state ^= state >>> 33;
        state *= 0xc4ceb9fe1a85ec53L;
        state ^= state >>> 33;
        return state;
    }
}
