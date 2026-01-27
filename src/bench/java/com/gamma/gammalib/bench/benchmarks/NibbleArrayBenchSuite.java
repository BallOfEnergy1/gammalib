package com.gamma.gammalib.bench.benchmarks;

import java.util.ArrayList;
import java.util.List;

import com.gamma.gammalib.bench.BenchmarkCase;
import com.gamma.gammalib.bench.BenchmarkConfig;
import com.gamma.gammalib.bench.BenchmarkSuite;
import com.gamma.gammalib.bench.impl.NibbleArrayImpl;

public final class NibbleArrayBenchSuite implements BenchmarkSuite {

    @Override
    public String name() {
        return "fast.nibblearray";
    }

    @Override
    public List<BenchmarkCase> cases(BenchmarkConfig config) {
        List<BenchmarkCase> cases = new ArrayList<>();
        for (NibbleArrayImpl impl : NibbleArrayImpl.values()) {
            if (!impl.isSupported()) {
                continue;
            }
            cases.add(new NibbleArrayBenchCase(impl, NibbleArrayBenchCase.Op.GET));
            cases.add(new NibbleArrayBenchCase(impl, NibbleArrayBenchCase.Op.GET_PARALLEL));
            cases.add(new NibbleArrayBenchCase(impl, NibbleArrayBenchCase.Op.SET));
            cases.add(new NibbleArrayBenchCase(impl, NibbleArrayBenchCase.Op.SET_PARALLEL));
            cases.add(new NibbleArrayBenchCase(impl, NibbleArrayBenchCase.Op.INC_AND_GET));
            cases.add(new NibbleArrayBenchCase(impl, NibbleArrayBenchCase.Op.INC_AND_GET_PARALLEL));
        }
        return cases;
    }
}
