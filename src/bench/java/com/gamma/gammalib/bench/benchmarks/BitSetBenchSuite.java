package com.gamma.gammalib.bench.benchmarks;

import java.util.ArrayList;
import java.util.List;

import com.gamma.gammalib.bench.BenchmarkCase;
import com.gamma.gammalib.bench.BenchmarkConfig;
import com.gamma.gammalib.bench.BenchmarkSuite;
import com.gamma.gammalib.bench.impl.BitSetImpl;

public final class BitSetBenchSuite implements BenchmarkSuite {

    @Override
    public String name() {
        return "fast.bitset";
    }

    @Override
    public List<BenchmarkCase> cases(BenchmarkConfig config) {
        List<BenchmarkCase> cases = new ArrayList<>();
        for (BitSetImpl impl : BitSetImpl.values()) {
            if (!impl.isSupported()) {
                continue;
            }
            cases.add(new BitSetBenchCase(impl, BitSetBenchCase.Op.GET));
            cases.add(new BitSetBenchCase(impl, BitSetBenchCase.Op.GET_PARALLEL));
            cases.add(new BitSetBenchCase(impl, BitSetBenchCase.Op.SET));
            cases.add(new BitSetBenchCase(impl, BitSetBenchCase.Op.SET_PARALLEL));
            cases.add(new BitSetBenchCase(impl, BitSetBenchCase.Op.CLEAR));
            cases.add(new BitSetBenchCase(impl, BitSetBenchCase.Op.CLEAR_PARALLEL));
            cases.add(new BitSetBenchCase(impl, BitSetBenchCase.Op.FLIP));
            cases.add(new BitSetBenchCase(impl, BitSetBenchCase.Op.FLIP_PARALLEL));
        }
        return cases;
    }
}
