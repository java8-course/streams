package part3.exercise;

import org.junit.Test;
import part2.exercise.CollectorsExercise2;
import part2.exercise.CollectorsExercise2.Key;
import part2.exercise.CollectorsExercise2.MapPair;
import part2.exercise.CollectorsExercise2.Value;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.mapping;
import static java.util.stream.Collectors.toMap;
import static org.junit.Assert.assertEquals;
import static part2.exercise.CollectorsExercise2.generatePairs;
import static part2.exercise.CollectorsExercise2.mapMerger;

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

    private static <T, M1, M2, R1, R2> Collector<T, Pair<M1, M2>, Pair<R1, R2>> paired(Collector<T, M1, R1> c1,
                                                                                       Collector<T, M2, R2> c2) {

        return new Collector<T, Pair<M1, M2>, Pair<R1, R2>>() {
            @Override
            public Supplier<Pair<M1, M2>> supplier() {
                return () -> new Pair<>(c1.supplier().get(), c2.supplier().get());
            }

            @Override
            public BiConsumer<Pair<M1, M2>, T> accumulator() {
                return (m1M2Pair, t) -> {
                    c1.accumulator().accept(m1M2Pair.getA(), t);
                    c2.accumulator().accept(m1M2Pair.getB(), t);
                };
            }

            @Override
            public BinaryOperator<Pair<M1, M2>> combiner() {
                return (m1M2Pair1, m1M2Pair2) -> {
                    M1 m1 = c1.combiner().apply(m1M2Pair1.getA(), m1M2Pair2.getA());
                    M2 m2 = c2.combiner().apply(m1M2Pair1.getB(), m1M2Pair2.getB());
                    return new Pair<>(m1, m2);
                };
            }

            @Override
            public Function<Pair<M1, M2>, Pair<R1, R2>> finisher() {
                return m1M2Pair -> {
                    R1 r1 = c1.finisher().apply(m1M2Pair.getA());
                    R2 r2 = c2.finisher().apply(m1M2Pair.getB());
                    return new Pair<>(r1, r2);
                };
            }

            @Override
            public Set<Characteristics> characteristics() {
                HashSet<Characteristics> ch = new HashSet<>();
                ch.addAll(c1.characteristics());
                ch.addAll(c2.characteristics());

                return ch;
            }
        };
    }

    @Test
    public void collectKeyValueMap() {
        // TODO see CollectorsExercise1::collectKeyValueMap
        // В 1 проход в 2 Map с использованием MapPair и mapMerger
        // final MapPair res2 = pairs.stream()
        //        .collect(new Collector<Pair, MapPair, MapPair>() {
        List<CollectorsExercise2.Pair> pairs = generatePairs(10, 100);

        final MapPair res1 =
                pairs.stream()
                        .collect(new Collector<CollectorsExercise2.Pair, MapPair, MapPair>() {
                            @Override
                            public Supplier<MapPair> supplier() {
                                return MapPair::new;
                            }

                            @Override
                            public BiConsumer<MapPair, CollectorsExercise2.Pair> accumulator() {
                                return (mapPair, pair) -> {
                                    mapPair.getKeyById().putIfAbsent(pair.getKey().getId(), pair.getKey());
                                    mapPair.getValueById().computeIfAbsent(pair.getValue().getKeyId(),
                                            s -> new ArrayList<>()).add(pair.getValue());
                                };
                            }

                            @Override
                            public BinaryOperator<MapPair> combiner() {
                                return (mapPair1, mapPair2) -> {
                                    BinaryOperator<Map<String, Key>> keyBinaryOperator = mapMerger((o, o2) -> o);
                                    Map<String, Key> mapKeys =
                                            keyBinaryOperator.apply(mapPair1.getKeyById(), mapPair2.getKeyById());

                                    BinaryOperator<Map<String, List<Value>>> valueBinaryOperator =
                                            mapMerger(
                                                    (list1, list2) -> {
                                                        list1.addAll(list2);
                                                        return list1;
                                                    });
                                    Map<String, List<Value>> mapValues =
                                            valueBinaryOperator.apply(mapPair1.getValueById(), mapPair2.getValueById());

                                    return new MapPair(mapKeys, mapValues);
                                };
                            }

                            @Override
                            public Function<MapPair, MapPair> finisher() {
                                return Function.identity();
                            }

                            @Override
                            public Set<Characteristics> characteristics() {
                                return Collections.unmodifiableSet(EnumSet.of(
                                        Characteristics.UNORDERED,
                                        Collector.Characteristics.IDENTITY_FINISH));
                            }
                        });


        // Перепишите решение в слещующем виде:
        final Pair<Map<String, Key>, Map<String, List<Value>>> res2 = pairs.stream()
                .collect(
                        paired(
                                mapping(CollectorsExercise2.Pair::getKey, toMap(Key::getId, Function.identity(), (x, y) -> x)),
                                mapping(CollectorsExercise2.Pair::getValue, groupingBy(Value::getKeyId))
                        )
                );

        res1.getKeyById().forEach((s, key) -> {
            String k = res2.getA().get(s).getId();
            assertEquals(s, k);
        });

        res1.getValueById().forEach((s, values) -> {
            List<Value> values2 = res2.getB().get(s);
            assertEquals(values.get(0), values2.get(0));
            assertEquals(values.size(), values2.size());
        });

    }

}
