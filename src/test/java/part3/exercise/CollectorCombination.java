package part3.exercise;

import part2.exercise.CollectorsExercise2;
import part2.exercise.CollectorsExercise2.Key;
import part2.exercise.CollectorsExercise2.MapPair;
import part2.exercise.CollectorsExercise2.Value;
import org.junit.Test;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
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
                    BiConsumer<Object, T> accumulator1 = (BiConsumer<Object, T>) c1.accumulator();
                    accumulator1.accept(pair.getA(), t);

                    BiConsumer<Object, T> accumulator2 = (BiConsumer<Object, T>) c2.accumulator();
                    accumulator2.accept(pair.getB(), t);
                };
            }

            @Override
            public BinaryOperator<Pair<?, ?>> combiner() {
                return (t1, t2) -> {
                    BinaryOperator<Object> operator1 = (BinaryOperator<Object>) c1.combiner();
                    Object m1 = operator1.apply(t1.getA(), t2.getA());

                    BinaryOperator<Object> operator2 = (BinaryOperator<Object>) c2.combiner();
                    Object m2 = operator2.apply(t1.getB(), t2.getB());

                    return new Pair<>(m1, m2);
                };
            }

            @Override
            public Function<Pair<?, ?>, Pair<R1, R2>> finisher() {

                return pair -> {
                    Function<Object, R1> finisher1 = (Function<Object, R1>) c1.finisher();
                    R1 r1 = finisher1.apply(pair.getA());

                    Function<Object, R2> finisher2 = (Function<Object, R2>) c2.finisher();
                    R2 r2 = finisher2.apply(pair.getB());

                    return new Pair<>(r1, r2);
                };
            }

            @Override
            public Set<Characteristics> characteristics() {
                return EnumSet.of(Characteristics.CONCURRENT, Characteristics.UNORDERED, Characteristics.IDENTITY_FINISH).stream()
                        .filter(c1.characteristics()::contains)
                        .filter(c2.characteristics()::contains)
                        .collect(Collectors.toSet());
            }
        };
    }

    @Test
    public void collectKeyValueMap() {

        final List<CollectorsExercise2.Pair> pairs = CollectorsExercise2.generatePairs(10, 100);

        //keyValuesMap using MapPairCollector
        final MapPair res1 = pairs.stream().collect(new MapPairCollector());

        final Map<String, Key> keyMap1 = res1.getKeyById();
        final Map<String, Set<Value>> valuesMap1 = res1.getValueById();

        final Map<Key, Set<Value>> keyValuesMap1 = valuesMap1.entrySet().stream()
                .filter(entry -> keyMap1.containsKey(entry.getKey()))
                .collect(
                        toMap(
                                entry -> keyMap1.get(entry.getKey()),
                                Map.Entry::getValue)
                );

        //keyValuesMap using paired method
        final Pair<Map<String, Key>, Map<String, Set<Value>>> res2 = pairs.stream()
                .collect(
                        paired(
                                mapping(CollectorsExercise2.Pair::getKey, toMap(Key::getId, Function.identity(), (x, y) -> x)),
                                mapping(CollectorsExercise2.Pair::getValue, groupingBy(Value::getKeyId, toSet()))
                        )
                );

        final Map<String, Key> keyMap2 = res2.getA();
        final Map<String, Set<Value>> valuesMap2 = res2.getB();

        final Map<Key, Set<Value>> keyValuesMap2 = valuesMap2.entrySet().stream()
                .filter(entry -> keyMap2.containsKey(entry.getKey()))
                .collect(
                        toMap(
                                entry -> keyMap2.get(entry.getKey()),
                                Map.Entry::getValue)
                );

        assertEquals(keyValuesMap1, keyValuesMap2);
    }
}
