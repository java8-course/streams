package bonus;

import java.util.EnumSet;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;

import static java.util.stream.Collector.Characteristics.*;

@SuppressWarnings("WeakerAccess")
public class CollectorToSet {
    public static <T> Collector<T, Set<T>, Set<T>> classic() {
        return new Collector<T, Set<T>, Set<T>>() {
            @Override
            public Supplier<Set<T>> supplier() {
                return HashSet::new;
            }

            @Override
            public BiConsumer<Set<T>, T> accumulator() {
                return Set::add;
            }

            @Override
            public BinaryOperator<Set<T>> combiner() {
                return (ts, ts2) -> {
                    ts.addAll(ts2);
                    return ts;
                };
            }

            @Override
            public Function<Set<T>, Set<T>> finisher() {
                return Function.identity();
            }

            @Override
            public Set<Characteristics> characteristics() {
                return EnumSet.of(UNORDERED, IDENTITY_FINISH);
            }
        };
    }

    public static <T> Collector<T, Map<T, Object>, Set<T>> concurrent() {
        return new Collector<T, Map<T, Object>, Set<T>>() {
            @Override
            public Supplier<Map<T, Object>> supplier() {
                return ConcurrentHashMap::new;
            }

            @Override
            public BiConsumer<Map<T, Object>, T> accumulator() {
                return (tMap, t) -> tMap.put(t, "");
            }

            @Override
            public BinaryOperator<Map<T, Object>> combiner() {
                return (tMap, tMap2) -> {
                    tMap.putAll(tMap2);
                    return tMap;
                };
            }

            @Override
            public Function<Map<T, Object>, Set<T>> finisher() {
                return Map::keySet;
            }

            @Override
            public Set<Characteristics> characteristics() {
                return EnumSet.of(UNORDERED, CONCURRENT);
            }
        };
    }

    public static <T> Collector<T, Map<T, Object>, Set<T>> singleAccumConcurrent() {
        return new Collector<T, Map<T, Object>, Set<T>>() {
            private Map<T, Object> globalCollector = new ConcurrentHashMap<>();

            @Override
            public Supplier<Map<T, Object>> supplier() {
                return () -> globalCollector;
            }

            @Override
            public BiConsumer<Map<T, Object>, T> accumulator() {
                return (x, t) -> globalCollector.put(t, "");
            }

            @Override
            public BinaryOperator<Map<T, Object>> combiner() {
                return (x1, x2) -> globalCollector;
            }

            @Override
            public Function<Map<T, Object>, Set<T>> finisher() {
                return x -> globalCollector.keySet();
            }

            @Override
            public Set<Characteristics> characteristics() {
                return EnumSet.of(UNORDERED, CONCURRENT);
            }
        };
    }
}
