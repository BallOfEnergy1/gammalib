package com.gamma.gammalib.bench;

public final class Blackhole {

    private long sink;

    public void consume(boolean value) {
        sink ^= value ? 0x9E3779B97F4A7C15L : 0xC2B2AE3D27D4EB4FL;
    }

    public void consume(int value) {
        sink ^= value * 0x9E3779B9L;
    }

    public void consume(long value) {
        sink ^= value * 0x9E3779B97F4A7C15L;
    }

    public void consume(byte value) {
        sink ^= (value & 0xFFL) * 0xC2B2AE3D27D4EB4FL;
    }

    public long value() {
        return sink;
    }
}
