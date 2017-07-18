package part3.exercise;

import org.junit.Test;
import part2.exercise.CollectorsExercise2;
import part2.exercise.CollectorsExercise2.Key;
import part2.exercise.CollectorsExercise2.Value;

import java.util.*;
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

        @Override
        public String toString() {
            return String.format("Pair<%s & %s>", a, b);
        }
    }

    private static <T, M1, M2, R1, R2> Collector<T, Pair<M1, M2>, Pair<R1, R2>> paired(Collector<T, M1, R1> c1,
                                                                                       Collector<T, M2, R2> c2) {
        // TODO
        return new Collector<T, Pair<M1, M2>, Pair<R1, R2>>() {
            @Override
            public Supplier<Pair<M1, M2>> supplier() {
                return () -> new Pair<>(c1.supplier().get(), c2.supplier().get());
            }

            @Override
            public BiConsumer<Pair<M1, M2>, T> accumulator() {
                return (pair, t) -> {
                    c1.accumulator().accept(pair.getA(), t);
                    c2.accumulator().accept(pair.getB(), t);
                };
            }

            @Override
            public BinaryOperator<Pair<M1, M2>> combiner() {
                return (p1, p2) -> new Pair<> (
                    c1.combiner().apply(p1.getA(), p2.getA()),
                    c2.combiner().apply(p1.getB(), p2.getB()));
            }

            @Override
            public Function<Pair<M1, M2>, Pair<R1, R2>> finisher() {
                return p -> new Pair<> (
                    c1.finisher().apply(p.getA()),
                    c2.finisher().apply(p.getB()));
            }

            @Override
            public Set<Characteristics> characteristics() {
                Set<Characteristics> set = new HashSet<>();
                set.addAll(c1.characteristics());
                set.addAll(c2.characteristics());
                return set;
            }
        };
    }

    @Test
    public void collectKeyValueMap() {
        final List<CollectorsExercise2.Pair> pairs = CollectorsExercise2.generatePairs(10, 100);

        // TODO see CollectorsExercise1::collectKeyValueMap
        // В 1 проход в 2 Map с использованием MapPair и mapMerger
         final Map<Key, List<Value>> res1 = pairs.stream()
                .collect(collectingAndThen(
                                new Collector<CollectorsExercise2.Pair, CollectorsExercise2.MapPair, CollectorsExercise2.MapPair>() {
                            @Override
                            public Supplier<CollectorsExercise2.MapPair> supplier() {
                                // TODO
                                return CollectorsExercise2.MapPair::new;
                            }

                            @Override
                            public BiConsumer<CollectorsExercise2.MapPair, CollectorsExercise2.Pair> accumulator() {
                                // TODO
                                return CollectorsExercise2.MapPair::put;
                            }

                            @Override
                            public BinaryOperator<CollectorsExercise2.MapPair> combiner() {
                                // TODO
                                return (mapPair, mapPair2) -> {
                                    BinaryOperator<Map<String, Key>> keyMerger = CollectorsExercise2.mapMerger((v1, v2) -> v1);
                                    keyMerger.apply(mapPair.getKeyById(), mapPair2.getKeyById());
                                    BinaryOperator<Map<String, List<Value>>> valueMerger =
                                            CollectorsExercise2.mapMerger((v1, v2) -> {
                                                v1.addAll(v2);
                                                return v1;
                                            });
                                    valueMerger.apply(mapPair.getValueById(), mapPair2.getValueById());
                                    return mapPair;
                                };
                            }

                            @Override
                            public Function<CollectorsExercise2.MapPair, CollectorsExercise2.MapPair> finisher() {
                                return Function.identity();
                            }

                            @Override
                            public Set<Characteristics> characteristics() {
                                return Collections.unmodifiableSet(EnumSet.of(
                                        Characteristics.UNORDERED,
                                        Characteristics.IDENTITY_FINISH));
                            }
                        },
                        (CollectorsExercise2.MapPair mmp) -> mmp.getKeyById().entrySet().stream()
                                .map(kp -> new AbstractMap.SimpleEntry<>(kp.getValue(), mmp.getValueById().get(kp.getKey())))
                                .collect(toMap(
                                        Map.Entry::getKey,
                                        Map.Entry::getValue
                                ))
                    )
                );

        // Перепишите решение в слещующем виде:
        final Map<Key, List<Value>> res2 = pairs.stream()
                .collect(
                        collectingAndThen(
                                paired(
                                        mapping(CollectorsExercise2.Pair::getKey,
                                                toMap(Key::getId, Function.identity(), (x, y) -> x)),
                                        mapping(CollectorsExercise2.Pair::getValue,
                                                groupingBy(Value::getKeyId))
                                ),
                                mmp -> mmp.getA().entrySet().stream()
                                            .map(kp -> new AbstractMap.SimpleEntry<>(kp.getValue(), mmp.getB().get(kp.getKey())))
                                            .collect(toMap(
                                                    Map.Entry::getKey,
                                                    Map.Entry::getValue
                                            ))
                        )
                );

        // TODO tests
        assertEquals(res1, res2);
    }
}
