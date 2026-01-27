package com.gamma.gammalib.bench;

public final class BenchmarkConfig {

    public final int size;
    public final int operations;
    public final int warmupIterations;
    public final int measureIterations;
    public final int threads;
    public final long seed;

    private BenchmarkConfig(int size, int operations, int warmupIterations, int measureIterations, int threads,
        long seed) {
        this.size = size;
        this.operations = operations;
        this.warmupIterations = warmupIterations;
        this.measureIterations = measureIterations;
        this.threads = threads;
        this.seed = seed;
    }

    public static BenchmarkConfig fromSystemProperties() {
        int size = readInt("bench.size", 1 << 20);
        int operations = readInt("bench.ops", 1 << 16);
        int warmup = readInt("bench.warmup", 15);
        int measure = readInt("bench.measure", 50);
        int threads = readInt(
            "bench.threads",
            Math.min(
                16,
                Runtime.getRuntime()
                    .availableProcessors() / 2));
        long seed = readLong("bench.seed", 0x5EED5EEDL);
        return new BenchmarkConfig(size, operations, warmup, measure, threads, seed);
    }

    private static int readInt(String key, int fallback) {
        String value = System.getProperty(key);
        if (value == null || value.isEmpty()) {
            return fallback;
        }
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException ignored) {
            return fallback;
        }
    }

    private static long readLong(String key, long fallback) {
        String value = System.getProperty(key);
        if (value == null || value.isEmpty()) {
            return fallback;
        }
        try {
            return Long.parseLong(value);
        } catch (NumberFormatException ignored) {
            return fallback;
        }
    }
}
