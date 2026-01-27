package com.gamma.gammalib.bench;

import com.gamma.gammalib.bench.benchmarks.BitSetBenchSuite;
import com.gamma.gammalib.bench.benchmarks.ByteArrayBenchSuite;
import com.gamma.gammalib.bench.benchmarks.NibbleArrayBenchSuite;

public final class BenchmarkMain {

    public static void main(String[] args) {
        BenchmarkConfig config = BenchmarkConfig.fromSystemProperties();
        System.out.printf(
            "Config: size=%d ops=%d warmup=%d measure=%d threads=%d seed=0x%X%n",
            config.size,
            config.operations,
            config.warmupIterations,
            config.measureIterations,
            config.threads,
            config.seed);
        BenchmarkRunner runner = new BenchmarkRunner(config, System.out);
        runner.runSuite(new BitSetBenchSuite());
        runner.runSuite(new ByteArrayBenchSuite());
        runner.runSuite(new NibbleArrayBenchSuite());
    }
}
