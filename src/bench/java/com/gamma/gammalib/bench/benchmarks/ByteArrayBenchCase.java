package com.gamma.gammalib.bench.benchmarks;

import com.gamma.gammalib.bench.BenchmarkCase;
import com.gamma.gammalib.bench.BenchmarkConfig;
import com.gamma.gammalib.bench.BenchmarkUtil;
import com.gamma.gammalib.bench.Blackhole;
import com.gamma.gammalib.bench.ImplConfigScope;
import com.gamma.gammalib.bench.impl.ByteArrayImpl;
import com.gamma.gammalib.multi.bytearray.FastAtomicByteArray;

final class ByteArrayBenchCase implements BenchmarkCase {

    enum Op {
        GET,
        SET,
        CAS,
        INC,
        GET_COPY,
        SET_ALL_0
    }

    private final ByteArrayImpl impl;
    private final Op op;
    private final String name;

    private ImplConfigScope scope;
    private FastAtomicByteArray array;
    private int[] indices;
    private byte[] valuesA;
    private byte[] valuesB;
    private long operations;
    private boolean casFlip;
    private byte invalidateValue;

    ByteArrayBenchCase(ByteArrayImpl impl, Op op) {
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
        casFlip = false;
        invalidateValue = 1;
        if (op == Op.GET_COPY || op == Op.SET_ALL_0) {
            operations = 1;
        }
    }

    @Override
    public void run(Blackhole blackhole) {
        switch (op) {
            case GET:
                for (int i = 0; i < indices.length; i++) {
                    blackhole.consume(array.get(indices[i]));
                }
                break;
            case SET:
                for (int i = 0; i < indices.length; i++) {
                    array.set(indices[i], valuesB[i]);
                }
                break;
            case CAS:
                int success = 0;
                byte[] expect = casFlip ? valuesB : valuesA;
                byte[] update = casFlip ? valuesA : valuesB;
                for (int i = 0; i < indices.length; i++) {
                    if (array.compareAndSet(indices[i], expect[i], update[i])) {
                        success++;
                    }
                }
                casFlip = !casFlip;
                blackhole.consume(success);
                break;
            case INC:
                for (int i = 0; i < indices.length; i++) {
                    blackhole.consume(array.incrementAndGet(indices[i]));
                }
                break;
            case GET_COPY:
                array.set(0, invalidateValue++);
                blackhole.consume(array.getCopy().length);
                break;
            case SET_ALL_0:
                array.setAll0();
                blackhole.consume(array.get(0));
                break;
        }
    }

    @Override
    public void teardown() {
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
