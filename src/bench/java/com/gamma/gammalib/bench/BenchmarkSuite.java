package com.gamma.gammalib.bench;

import java.util.List;

public interface BenchmarkSuite {

    String name();

    List<BenchmarkCase> cases(BenchmarkConfig config);
}
