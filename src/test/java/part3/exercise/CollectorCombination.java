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

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            Pair<?, ?> pair = (Pair<?, ?>) o;

            if (!a.equals(pair.a)) return false;
            return b.equals(pair.b);
        }

        @Override
        public int hashCode() {
            int result = a.hashCode();
            result = 31 * result + b.hashCode();
            return result;
        }
    }

    private static <T, M1, M2, R1, R2> Collector<T, Pair<M1, M2>, Pair<R1, R2>> paired(Collector<T, M1, R1> c1,
                                                                                       Collector<T, M2, R2> c2) {
        return new Collector<T, Pair<M1, M2>, Pair<R1, R2>>() {
            @Override
            public Supplier<Pair<M1, M2>> supplier() {
                return () -> new Pair<>(
                        c1.supplier().get(),
                        c2.supplier().get()
                );
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
                return (m1M2Pair1, m1M2Pair2) -> new Pair<>(
                        c1.combiner().apply(m1M2Pair1.getA(), m1M2Pair2.getA()),
                        c2.combiner().apply(m1M2Pair1.getB(), m1M2Pair2.getB())
                );
            }

            @Override
            public Function<Pair<M1, M2>, Pair<R1, R2>> finisher() {
                return m1M2Pair -> new Pair<>(
                        c1.finisher().apply(m1M2Pair.getA()),
                        c2.finisher().apply(m1M2Pair.getB())
                );
            }

            @Override
            public Set<Characteristics> characteristics() {
                return new HashSet<>();
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

        final Pair<Map<String, Key>, Map<String, List<Value>>> res1;

        Map<String, Key> collect = pairs.stream().collect(mapping(CollectorsExercise2.Pair::getKey, toMap(Key::getId,
                Function.identity(),
                (x, y) -> x))
        );

        Map<String, List<Value>> collect1 = pairs.stream().collect(mapping(CollectorsExercise2.Pair::getValue,
                groupingBy(Value::getKeyId)));

        res1 = new Pair<>(collect, collect1);

        final Pair<Map<String, Key>, Map<String, List<Value>>> res2 = pairs.stream()
                .collect(paired(mapping(CollectorsExercise2.Pair::getKey,
                        toMap(Key::getId,
                                Function.identity(),
                                (x, y) -> x)),
                        mapping(CollectorsExercise2.Pair::getValue,
                                groupingBy(Value::getKeyId))));

        assertEquals(res1, res2);
    }

}
