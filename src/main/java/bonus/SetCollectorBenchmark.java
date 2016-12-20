package bonus;

import org.openjdk.jmh.annotations.*;

import java.util.Arrays;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

@Fork(1)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@Warmup(iterations = 5, time = 3, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 5, time = 10, timeUnit = TimeUnit.SECONDS)
@State(Scope.Thread)
public class SetCollectorBenchmark {
    @Param({"1000", "10000", "100000", "1000000"})      // Values for benchmarks
    private int collectionSize;

    private String[] lotsOfStrings;

    public SetCollectorBenchmark() {
    }

    SetCollectorBenchmark(int collectionSize) {
        this.collectionSize = collectionSize;
        setupBenchmark();
    }

    @Setup
    public void setupBenchmark() {
        lotsOfStrings = IntStream.generate(() -> ThreadLocalRandom.current().nextInt(collectionSize))
                .limit(collectionSize)
                .boxed()
                .map(Object::toString)
                .toArray(String[]::new);
    }

    @Benchmark
    public Set<String> collectNonConcurrentSet() {
        return Arrays.stream(lotsOfStrings).parallel().collect(CollectorToSet.classic());
    }

    @Benchmark
    public Set<String> collectConcurrentSet() {
        return Arrays.stream(lotsOfStrings).parallel().collect(CollectorToSet.concurrent());
    }

    @Benchmark
    public Set<String> collectConcurrentSetSingleAccum() {
        return Arrays.stream(lotsOfStrings).parallel().collect(CollectorToSet.singleAccumConcurrent());
    }

}
