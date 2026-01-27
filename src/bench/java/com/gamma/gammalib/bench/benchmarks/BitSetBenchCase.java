package com.gamma.gammalib.bench.benchmarks;

import java.util.function.Consumer;

import com.gamma.gammalib.bench.BenchmarkConfig;
import com.gamma.gammalib.bench.BenchmarkUtil;
import com.gamma.gammalib.bench.Blackhole;
import com.gamma.gammalib.bench.ImplConfigScope;
import com.gamma.gammalib.bench.impl.BitSetImpl;
import com.gamma.gammalib.bench.parallel.ParallelCase;
import com.gamma.gammalib.multi.bitset.FastAtomicBitSet;

final class BitSetBenchCase extends ParallelCase {

    enum Op {

        GET,
        GET_PARALLEL(true),
        SET,
        SET_PARALLEL(true),
        CLEAR,
        CLEAR_PARALLEL(true),
        FLIP,
        FLIP_PARALLEL(true);

        boolean parallel = false;

        Op() {}

        Op(boolean parallel) {
            this.parallel = parallel;
        }
    }

    private final BitSetImpl impl;
    private final Op op;
    private final String name;

    private ImplConfigScope scope;
    private FastAtomicBitSet bitSet;
    private int[] indices;
    private long operations;

    BitSetBenchCase(BitSetImpl impl, Op op) {
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
        bitSet = impl.create(config.size);
        indices = BenchmarkUtil.generateIndices(config.size, config.operations, config.seed ^ op.ordinal());

        operations = config.operations;
        if (op == Op.GET || op == Op.GET_PARALLEL) {
            for (int index : indices) {
                bitSet.set(index);
            }
        }
        if (op.parallel) {
            Consumer<Blackhole> task = switch (op) {
                case GET_PARALLEL -> (blackhole) -> {
                    for (int i : indices) {
                        blackhole.consume(bitSet.get(i));
                    }
                };
                case SET_PARALLEL -> (_) -> {
                    for (int i : indices) {
                        bitSet.set(i);
                    }
                };
                case CLEAR_PARALLEL -> (_) -> {
                    for (int i : indices) {
                        bitSet.clear(i);
                    }
                };
                case FLIP_PARALLEL -> (_) -> {
                    for (int i : indices) {
                        bitSet.flip(i);
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
                for (int index : indices) {
                    blackhole.consume(bitSet.get(index));
                }
                break;
            case SET:
                for (int index : indices) {
                    bitSet.set(index);
                }
                break;
            case CLEAR:
                for (int index : indices) {
                    bitSet.clear(index);
                }
                break;
            case FLIP:
                for (int index : indices) {
                    bitSet.flip(index);
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
        bitSet = null;
        indices = null;
    }
}
