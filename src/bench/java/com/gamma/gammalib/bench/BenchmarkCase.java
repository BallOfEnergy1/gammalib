package com.gamma.gammalib.bench;

public interface BenchmarkCase {

    String name();

    String label();

    long operationsPerRun();

    void setup(BenchmarkConfig config);

    void run(Blackhole blackhole);

    void teardown();
}
