package com.gamma.gammalib.bench;

import java.io.PrintStream;

public final class BenchmarkRunner {

    private final BenchmarkConfig config;
    private final PrintStream out;
    private final Blackhole blackhole = new Blackhole();

    public BenchmarkRunner(BenchmarkConfig config, PrintStream out) {
        this.config = config;
        this.out = out;
    }

    public void runSuite(BenchmarkSuite suite) {
        out.printf("== %s ==%n", suite.name());
        String lastLabel = "";
        for (BenchmarkCase benchCase : suite.cases(config)) {
            if (!lastLabel.equals(benchCase.label())) {
                out.println(lastLabel = benchCase.label());
            }
            runCase(benchCase);
        }
        out.println();
    }

    private void runCase(BenchmarkCase benchCase) {
        benchCase.setup(config);
        try {
            for (int i = 0; i < config.warmupIterations; i++) {
                benchCase.run(blackhole);
            }
            long start = System.nanoTime();
            for (int i = 0; i < config.measureIterations; i++) {
                benchCase.run(blackhole);
            }
            long elapsed = System.nanoTime() - start;
            long ops = benchCase.operationsPerRun() * (long) config.measureIterations;
            double nsPerOp = ops == 0 ? 0.0d : ((double) elapsed) / (double) ops;
            double opsPerSec = nsPerOp == 0.0d ? 0.0d : (1_000_000_000.0d / nsPerOp);
            out.printf("\t%-42s %12.3f ns/op  %12.2f ops/s%n", benchCase.name(), nsPerOp, opsPerSec);
        } finally {
            benchCase.teardown();
        }
    }
}
