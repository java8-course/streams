package part3.exercise;

import org.junit.Test;
import part2.exercise.CollectorsExercise2;
import part2.exercise.CollectorsExercise2.Key;
import part2.exercise.CollectorsExercise2.Value;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;

import static java.util.stream.Collectors.*;

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
                return () -> new Pair<M1, M2>(c1.supplier().get(), c2.supplier().get());
            }

            @Override
            public BiConsumer<Pair<M1, M2>, T> accumulator() {
                return (pair, element) -> {
                    c1.accumulator().accept(pair.getA(), element);
                    c2.accumulator().accept(pair.getB(), element);
                };
            }

            @Override
            public BinaryOperator<Pair<M1, M2>> combiner() {
                return (pair1, pair2) -> {
                    M1 res1 = c1.combiner().apply(pair1.getA(), pair2.getA());
                    M2 res2 = c2.combiner().apply(pair2.getB(), pair2.getB());
                    return new Pair<M1, M2>(res1, res2);
                };
            }

            @Override
            public Function<Pair<M1, M2>, Pair<R1, R2>> finisher() {
                return (pair) -> {
                    R1 res1 = c1.finisher().apply(pair.getA());
                    R2 res2 = c2.finisher().apply(pair.getB());
                    return new Pair<>(res1, res2);
                };
            }

            @Override
            public Set<Characteristics> characteristics() {
                return null;
            }
        };
    }

    @Test
    public void collectKeyValueMap() {

        final List<CollectorsExercise2.Pair> pairs = CollectorsExercise2.generatePairs(10, 100);

        final CollectorsExercise2.MapPair res1 = pairs.stream()
                .collect(new CollectorsExercise2.KeyValueMappingCollector());

        final Map<String, Key> keyMap2 = res1.getKeyById();
        final Map<String, List<Value>> valuesMap2 = res1.getValueById();
//
//        final Map<Key, List<Value>> keyValuesMap2 = valuesMap1.entrySet()
//                .stream()
//                .collect(toMap(ent -> keyMap2.get(ent.getKey()), Map.Entry::getValue));

        final Pair<Map<String, Key>, Map<String, List<Value>>> res2 = pairs.stream()
                .collect(
                        paired(
                                mapping(CollectorsExercise2.Pair::getKey, toMap(Key::getId, Function.identity(), (x, y) -> x)),
                                mapping(CollectorsExercise2.Pair::getValue, groupingBy(Value::getKeyId))
                        )
                );


        // TODO tests
        throw new UnsupportedOperationException();
    }

}
