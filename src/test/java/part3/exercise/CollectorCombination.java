package part3.exercise;

import data.Person;
import org.junit.Test;
import part2.exercise.CollectorsExercise1;
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
                return (pair, t) -> {
                    c1.accumulator().accept(pair.getA(), t);
                    c2.accumulator().accept(pair.getB(), t);
                };
            }

            @Override
            public BinaryOperator<Pair<M1, M2>> combiner() {
                return (pair1, pair2) ->
                        new Pair<>(c1.combiner().apply(pair1.getA(), pair2.getA()), c2.combiner().apply(pair1.getB(), pair2.getB()));
            }

            @Override
            public Function<Pair<M1, M2>, Pair<R1, R2>> finisher() {
                return pair -> new Pair<>(c1.finisher().apply(pair.getA()), c2.finisher().apply(pair.getB()));
            }

            @Override
            public Set<Characteristics> characteristics() {
                return Collections.emptySet();
            }
        };
    }

    @Test
    public void collectKeyValueMap() {
        final List<CollectorsExercise2.Pair> pairs = CollectorsExercise2.generatePairs(10, 100);

        final Pair<Map<String, Key>, Map<String, List<Value>>> res2 = pairs.stream()
                .collect(
                        paired(
                                mapping(CollectorsExercise2.Pair::getKey, toMap(Key::getId, Function.identity(), (x, y) -> x)),
                                mapping(CollectorsExercise2.Pair::getValue, groupingBy(Value::getKeyId))
                        )
                );
    }
}
