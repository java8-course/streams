package bonus;

import org.openjdk.jmh.annotations.*;

import java.util.Arrays;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

@Fork(5)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
//@Warmup(iterations = 20, time = 1, timeUnit = TimeUnit.MINUTES)
//@Measurement(iterations = 20, time = 1, timeUnit = TimeUnit.MINUTES)
@State(Scope.Thread)
public class SetCollectorBenchmark {
    private final String[] millionInts = IntStream.generate(() -> ThreadLocalRandom.current().nextInt(1_000_000))
            .limit(1_000_000)
            .boxed()
            .map(Object::toString)
            .toArray(String[]::new);

    @Benchmark
    public Set<String> collectSet() {
        return Arrays.stream(millionInts).collect(new CollectorToSet<>());
    }

    @Benchmark
    public Set<String> collectConcurrentSet() {
        return Arrays.stream(millionInts).collect(new ConcurrentCollectorToSet<>());
    }

}
