package com.gamma.gammalib.bench.parallel;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

import com.gamma.gammalib.bench.BenchmarkCase;
import com.gamma.gammalib.bench.Blackhole;

public abstract class ParallelCase implements BenchmarkCase {

    private static List<Runnable> tasks;
    private static ForkJoinPool pool;

    private static CountDownLatch latch;

    private static Blackhole blackhole;

    public void prepare(int numThreads) {
        pool = new ForkJoinPool(numThreads);
        latch = new CountDownLatch(1);
    }

    public void load(int numTasks, Consumer<Blackhole> task) {
        if (tasks == null) tasks = new ArrayList<>();
        for (int idx = 0; idx < numTasks; idx++) {
            Runnable wrappedTask = () -> {
                try {
                    latch.await();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                task.accept(blackhole);
            };
            tasks.add(wrappedTask);
        }
    }

    public void start(Blackhole blackhole) {
        ParallelCase.blackhole = blackhole;
        pool = new ForkJoinPool(pool.getParallelism());
        for (Runnable task : tasks) {
            pool.execute(task);
        }
        latch.countDown();
    }

    public void waitForFinish() {
        try {
            pool.shutdown();
            boolean finished = pool.awaitTermination(60, TimeUnit.SECONDS);
            if (!finished) throw new RuntimeException("Timed out waiting for pool to finish.");
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void teardown() {
        pool = null;
        latch = null;
        blackhole = null;
        tasks = null;
    }
}
