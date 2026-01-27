package com.gamma.gammalib.bench.benchmarks;

import java.util.ArrayList;
import java.util.List;

import com.gamma.gammalib.bench.BenchmarkCase;
import com.gamma.gammalib.bench.BenchmarkConfig;
import com.gamma.gammalib.bench.BenchmarkSuite;
import com.gamma.gammalib.bench.impl.ByteArrayImpl;

public final class ByteArrayBenchSuite implements BenchmarkSuite {

    @Override
    public String name() {
        return "fast.bytearray";
    }

    @Override
    public List<BenchmarkCase> cases(BenchmarkConfig config) {
        List<BenchmarkCase> cases = new ArrayList<>();
        for (ByteArrayImpl impl : ByteArrayImpl.values()) {
            if (!impl.isSupported()) {
                continue;
            }
            cases.add(new ByteArrayBenchCase(impl, ByteArrayBenchCase.Op.GET));
            cases.add(new ByteArrayBenchCase(impl, ByteArrayBenchCase.Op.SET));
            cases.add(new ByteArrayBenchCase(impl, ByteArrayBenchCase.Op.CAS));
            cases.add(new ByteArrayBenchCase(impl, ByteArrayBenchCase.Op.INC));
            cases.add(new ByteArrayBenchCase(impl, ByteArrayBenchCase.Op.GET_COPY));
            cases.add(new ByteArrayBenchCase(impl, ByteArrayBenchCase.Op.SET_ALL_0));
        }
        return cases;
    }
}
