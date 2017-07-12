package part3.exercise;

import org.junit.Test;
import part2.exercise.CollectorsExercise2;
import part2.exercise.CollectorsExercise2.Key;
import part2.exercise.CollectorsExercise2.Value;

import java.util.HashSet;
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

    private static <T, M1, M2, R1, R2> Collector<T, Pair<M1, M2>, Pair<R1, R2>> paired(Collector<T, M1, R1> c1,
                                                                                       Collector<T, M2, R2> c2) {
        return new Collector<T, Pair<M1, M2>, Pair<R1, R2>>() {
            @Override
            public Supplier<Pair<M1, M2>> supplier() {
                return () -> new Pair<>(c1.supplier().get(), c2.supplier().get());
            }

            @Override
            public BiConsumer<Pair<M1, M2>, T> accumulator() {
                return (p, e) -> {
                    c1.accumulator().accept(p.getA(), e);
                    c2.accumulator().accept(p.getB(), e);
                };
            }

            @Override
            public BinaryOperator<Pair<M1, M2>> combiner() {
                return (l, r) -> new Pair<>(c1.combiner().apply(l.getA(), r.getA()),
                        c2.combiner().apply(l.getB(), r.getB()));
            }

            @Override
            public Function<Pair<M1, M2>, Pair<R1, R2>> finisher() {
                return (p) -> new Pair<>(c1.finisher().apply(p.getA()), c2.finisher().apply(p.getB()));
            }

            @Override
            public Set<Characteristics> characteristics() {
                final Set<Characteristics> tmp = new HashSet<>(c1.characteristics());
                tmp.retainAll(c2.characteristics());
                return tmp;
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

        final Pair<Map<String, Key>, Map<String, List<Value>>> res2 = pairs.stream()
                .collect(
                        paired(
                                mapping(CollectorsExercise2.Pair::getKey, toMap(Key::getId, Function.identity(), (x, y) -> x)),
                                mapping(CollectorsExercise2.Pair::getValue, groupingBy(Value::getKeyId))
                        )
                );

        final Map<String, Key> keyMap1 = pairs
                .stream()
                .map(CollectorsExercise2.Pair::getKey)
                .collect(toMap(Key::getId, Function.identity(), (a, b) -> a));

        final Map<String, List<Value>> valueMap1 = pairs
                .stream()
                .map(CollectorsExercise2.Pair::getValue)
                .collect(groupingBy(Value::getKeyId));

        assertEquals(res2.getA(), keyMap1);
        assertEquals(res2.getB(), valueMap1);

    }

}
