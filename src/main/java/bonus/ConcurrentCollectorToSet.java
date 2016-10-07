package bonus;

import java.util.EnumSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;

import static java.util.stream.Collector.Characteristics.CONCURRENT;
import static java.util.stream.Collector.Characteristics.UNORDERED;

public class ConcurrentCollectorToSet<T> implements Collector<T, Map<T, Object>, Set<T>> {
    private Map<T, Object> globalCollector = new ConcurrentHashMap<>();

    @Override
    public Supplier<Map<T, Object>> supplier() {
        return () -> globalCollector;
    }

    @Override
    public BiConsumer<Map<T, Object>, T> accumulator() {
        return (ts, t) -> globalCollector.put(t, null);
    }

    @Override
    public BinaryOperator<Map<T, Object>> combiner() {
        return (ts, ts2) -> globalCollector;
    }

    @Override
    public Function<Map<T, Object>, Set<T>> finisher() {
        return s -> globalCollector.keySet();
    }

    @Override
    public Set<Characteristics> characteristics() {
        return EnumSet.of(UNORDERED, CONCURRENT);
    }
}
