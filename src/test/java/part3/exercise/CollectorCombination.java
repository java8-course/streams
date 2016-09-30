package part3.exercise;

import org.junit.Test;
import part2.exercise.CollectorsExercise2;
import part2.exercise.CollectorsExercise2.Key;
import part2.exercise.CollectorsExercise2.Value;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;

import static java.util.stream.Collectors.*;
import static org.junit.Assert.assertEquals;

public class CollectorCombination {

    private static class Pair<A, B> {
        private final A a;
        private final B b;

        public Pair(A a, B b) {
            this.a = a;
            this.b = b;
        }

        public A getA() {
            return a;
        }

        public B getB() {
            return b;
        }
    }

    private static <T, R1, R2, Q1, Q2> Collector<T, Pair<Q1, Q2>, Pair<R1, R2>> paired(Collector<T, Q1, R1> c1,
                                                                                       Collector<T, Q2, R2> c2) {
        return new Collector<T, Pair<Q1, Q2>, Pair<R1, R2>>() {
            @Override
            public Supplier<Pair<Q1, Q2>> supplier() {
                return () -> new Pair<>(
                        c1.supplier().get(),
                        c2.supplier().get()
                );
            }

            @Override
            public BiConsumer<Pair<Q1, Q2>, T> accumulator() {
                return (pair, t) -> {
                    c1.accumulator().accept(pair.getA(), t);
                    c2.accumulator().accept(pair.getB(), t);
                };

            }

            @Override
            public BinaryOperator<Pair<Q1, Q2>> combiner() {
                return (pair1, pair2) -> {
                    final Q1 combined1 = c1.combiner().apply(pair1.getA(), pair2.getA());
                    final Q2 combined2 = c2.combiner().apply(pair1.getB(), pair2.getB());
                    return new Pair<>(combined1, combined2);
                };
            }

            @Override
            public Function<Pair<Q1, Q2>, Pair<R1, R2>> finisher() {
                return pair -> {
                    final R1 result1 = c1.finisher().apply(pair.getA());
                    final R2 result2 = c2.finisher().apply(pair.getB());
                    return new Pair<>(result1, result2);
                };
            }

            @Override
            public Set<Characteristics> characteristics() {
                return Collections.emptySet();                  // Important!
            }
        };
    }

    @Test
    public void collectKeyValueMap() {
        final List<CollectorsExercise2.Pair> pairs = CollectorsExercise2.generatePairs(10, 100);

        final Pair<Map<String, Key>, Map<String, Set<Value>>> res2 = pairs.stream()
                .collect(
                        paired(
                                mapping(CollectorsExercise2.Pair::getKey, toMap(Key::getId, Function.identity(), (x, y) -> x)),
                                mapping(CollectorsExercise2.Pair::getValue, groupingBy(Value::getKeyId, toSet()))
                        )
                );

        final Map<Key, Set<Value>> actual = res2.getB().entrySet().stream()
                .collect(toMap(entry -> res2.getA().get(entry.getKey()), Map.Entry::getValue));

        final Map<Key, Set<Value>> expected = CollectorsExercise2.collectKeysValuesMode0(pairs, Collections.emptySet());

        assertEquals(expected, actual);
    }

}
