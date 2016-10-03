package part3.exercise;

import part2.exercise.CollectorsExercise2;
import part2.exercise.CollectorsExercise2.Key;
import part2.exercise.CollectorsExercise2.MapPairCollector;
import part2.exercise.CollectorsExercise2.Value;
import org.junit.Test;

import java.util.*;
import java.util.function.*;
import java.util.stream.Collector;
import java.util.stream.Collectors;

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

    private static <T, R1, R2> Collector<T, Pair<?, ?>, Pair<R1, R2>> paired(Collector<T, ?, R1> c1,
                                                                             Collector<T, ?, R2> c2) {
        return new Collector<T, Pair<?, ?>, Pair<R1, R2>>() {
            @Override
            public Supplier<Pair<?, ?>> supplier() {
                return () -> new Pair<>(c1.supplier().get(), c2.supplier().get());
            }

            @Override
            public BiConsumer<Pair<?, ?>, T> accumulator() {
                return (pair, t) -> {
                    BiConsumer<Object, T> a1 = (BiConsumer<Object, T>) c1.accumulator();
                    a1.accept(pair.getA(), t);
                    BiConsumer<Object, T> a2 = (BiConsumer<Object, T>) c2.accumulator();
                    a2.accept(pair.getB(), t);
                };
            }

            @Override
            public BinaryOperator<Pair<?, ?>> combiner() {
                return (t1, t2) -> {
                    BinaryOperator<Object> a1 = (BinaryOperator<Object>) c1.combiner();
                    Object m1 = a1.apply(t1.getA(), t2.getA());
                    BinaryOperator<Object> a2 = (BinaryOperator<Object>) c2.combiner();
                    Object m2 = a2.apply(t1.getB(), t2.getB());
                    return new Pair<>(m1, m2);
                };
            }

            @Override
            public Function<Pair<?, ?>, Pair<R1, R2>> finisher() {
                return pair -> {
                    Function<Object, R1> fin1 = (Function<Object, R1>) c1.finisher();
                    Function<Object, R2> fin2 = (Function<Object, R2>) c2.finisher();
                    R1 r1 = fin1.apply(pair.getA());
                    R2 r2 = fin2.apply(pair.getB());
                    return new Pair<>(r1, r2);
                };
            }

            @Override
            public Set<Characteristics> characteristics() {
                return EnumSet.of(Characteristics.CONCURRENT, Characteristics.UNORDERED, Characteristics.IDENTITY_FINISH).stream()
                        .filter(c1.characteristics()::contains)
                        .filter(c2.characteristics()::contains)
                        .collect(toSet());
            }
        };
    }

    @Test
    public void collectKeyValueMap() {
        // TODO see CollectorsExercise1::collectKeyValueMap
        // В 1 проход в 2 Map с использованием MapPair и mapMerger
        // final MapPair res2 = pairs.stream()
        //        .collect(new Collector<Pair, MapPair, MapPair>() {

        // Перепишите решение в слещующем виде:
        final List<CollectorsExercise2.Pair> pairs = CollectorsExercise2.generatePairs(10, 100);


        CollectorsExercise2.MapPair mapPair = pairs.parallelStream().collect(new MapPairCollector());
        Map<String, Key> keyById = mapPair.getKeyById();
        Map<String, List<Value>> valueById = mapPair.getValueById();
        Map<Key, List<Value>> expected = valueById.entrySet().stream()
                .filter(entry -> keyById.containsKey(entry.getKey()))
                .collect(
                        toMap(
                                entry -> keyById.get(entry.getKey()),
                                Map.Entry::getValue
                        )
                );


        final Pair<Map<String, Key>, Map<String, List<Value>>> res2 = pairs.parallelStream()
                .collect(
                        paired(
                                mapping(CollectorsExercise2.Pair::getKey, toMap(Key::getId, Function.identity(), (x, y) -> x)),
                                mapping(CollectorsExercise2.Pair::getValue, groupingBy(Value::getKeyId, toList()))
                        )
                );


        final Map<String, Key> res2A = res2.getA();
        final Map<String, List<Value>> res2B = res2.getB();

        Map<Key, List<Value>> collect = res2B.entrySet().stream()
                .filter(entry -> res2A.containsKey(entry.getKey()))
                .collect(
                        toMap(
                                entry -> res2A.get(entry.getKey()),
                                Map.Entry::getValue
                        )
                );

        assertEquals(expected, collect);
        // TODO tests
    }

}
