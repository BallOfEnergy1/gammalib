package com.gamma.gammalib.bench.benchmarks;

import java.util.function.Consumer;

import com.gamma.gammalib.bench.BenchmarkConfig;
import com.gamma.gammalib.bench.BenchmarkUtil;
import com.gamma.gammalib.bench.Blackhole;
import com.gamma.gammalib.bench.ImplConfigScope;
import com.gamma.gammalib.bench.impl.NibbleArrayImpl;
import com.gamma.gammalib.bench.parallel.ParallelCase;
import com.gamma.gammalib.multi.nibblearray.FastAtomicNibbleArray;

final class NibbleArrayBenchCase extends ParallelCase {

    enum Op {

        GET,
        GET_PARALLEL(true),
        SET,
        SET_PARALLEL(true),
        INC_AND_GET,
        INC_AND_GET_PARALLEL(true);

        boolean parallel = false;

        Op() {}

        Op(boolean parallel) {
            this.parallel = parallel;
        }
    }

    private final NibbleArrayImpl impl;
    private final Op op;
    private final String name;

    private ImplConfigScope scope;
    private FastAtomicNibbleArray array;
    private int[] indices;
    private byte[] valuesA;
    private byte[] valuesB;
    private long operations;

    NibbleArrayBenchCase(NibbleArrayImpl impl, Op op) {
        this.impl = impl;
        this.op = op;
        this.name = impl.label() + "/"
            + op.name()
                .toLowerCase();
    }

    @Override
    public String name() {
        return name;
    }

    @Override
    public String label() {
        return impl.label();
    }

    @Override
    public long operationsPerRun() {
        return operations;
    }

    @Override
    public void setup(BenchmarkConfig config) {
        scope = impl.applyConfig();
        array = impl.create(config.size);
        indices = BenchmarkUtil.generateIndices(config.size, config.operations, config.seed ^ op.ordinal());
        valuesA = BenchmarkUtil.generateBytes(config.operations, config.seed ^ 0xA5A5A5A5L);
        valuesB = BenchmarkUtil.generateBytes(config.operations, config.seed ^ 0x5A5A5A5AL);
        operations = config.operations;
        for (int i = 0; i < indices.length; i++) {
            array.set(indices[i], valuesA[i]);
        }

        if (op.parallel) {
            Consumer<Blackhole> task = switch (op) {
                case GET_PARALLEL -> (blackhole) -> {
                    for (int i : indices) {
                        blackhole.consume(array.get(i));
                    }
                };
                case SET_PARALLEL -> (_) -> {
                    for (int i : indices) {
                        array.set(i, valuesB[i % valuesB.length]);
                    }
                };
                case INC_AND_GET_PARALLEL -> (_) -> {
                    for (int i : indices) {
                        array.incrementAndGet(i);
                    }
                };
                default -> throw new UnsupportedOperationException();
            };

            prepare(config.threads);
            load(config.threads, task);
        }
    }

    @Override
    public void run(Blackhole blackhole) {
        if (op.parallel) {
            start(blackhole);
            waitForFinish();
            return;
        }
        switch (op) {
            case GET:
                for (int i = 0; i < indices.length; i++) {
                    blackhole.consume(array.get(indices[i]));
                }
                break;
            case SET:
                for (int i = 0; i < indices.length; i++) {
                    array.set(indices[i], valuesB[i % valuesB.length]);
                }
                break;
            case INC_AND_GET:
                for (int i = 0; i < indices.length; i++) {
                    blackhole.consume(array.incrementAndGet(indices[i]));
                }
                break;
        }
    }

    @Override
    public void teardown() {
        super.teardown();
        if (scope != null) {
            scope.close();
            scope = null;
        }
        array = null;
        indices = null;
        valuesA = null;
        valuesB = null;
    }
}
