package part3.exercise;

import org.junit.Test;
import part2.exercise.CollectorsExercise2;
import part2.exercise.CollectorsExercise2.Key;
import part2.exercise.CollectorsExercise2.Value;

import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;

import static java.util.stream.Collectors.*;
import static org.hamcrest.Matchers.isIn;
import static org.hamcrest.core.Every.everyItem;
import static org.junit.Assert.assertThat;
import static part2.exercise.CollectorsExercise2.collectKeyValueMapPair;
import static part2.exercise.CollectorsExercise2.generatePairs;

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
                return () -> new Pair<>(
                            c1.supplier().get(),
                            c2.supplier().get());
            }

            @Override
            public BiConsumer<Pair<?, ?>, T> accumulator() {
                return (p, t) -> {
                    final BiConsumer<Object, T> accumulator1 = (BiConsumer<Object, T>) c1.accumulator();
                    final BiConsumer<Object, T> accumulator2 = (BiConsumer<Object, T>) c2.accumulator();

                    accumulator1.accept(p.getA(), t);
                    accumulator2.accept(p.getB(), t);
                };
            }

            @Override
            public BinaryOperator<Pair<?, ?>> combiner() {
                return (p1, p2) -> {
                    final BinaryOperator<Object> combiner1 = (BinaryOperator<Object>) c1.combiner();
                    final BinaryOperator<Object> combiner2 = (BinaryOperator<Object>) c2.combiner();

                    return new Pair<>(
                            combiner1.apply(p1.getA(), p2.getA()),
                            combiner2.apply(p1.getB(), p2.getB()));
                };
            }

            @Override
            public Function<Pair<?, ?>, Pair<R1, R2>> finisher() {
                return p -> {
                    final Function<Object, R1> finisher1 = (Function<Object, R1>) c1.finisher();
                    final Function<Object, R2> finisher2 = (Function<Object, R2>) c2.finisher();

                    return new Pair<>(
                            finisher1.apply(p.getA()),
                            finisher2.apply(p.getB()));
                };
            }

            @Override
            public Set<Characteristics> characteristics() {
                return EnumSet.of(Characteristics.CONCURRENT, Characteristics.UNORDERED, Characteristics.IDENTITY_FINISH).stream()
                        .filter(c1.characteristics()::contains)
                        .filter(c1.characteristics()::contains)
                        .collect(toSet());
            }
        };
    }

    @Test
    public void collectKeyValueMap() {
        final List<CollectorsExercise2.Pair> pairs = generatePairs(10, 100);
        final Map<Key, List<Value>> expected = collectKeyValueMapPair(pairs);

        final Pair<Map<String, Key>, Map<String, List<Value>>> pairMap = pairs.stream()
                .collect(
                        paired(
                                mapping(CollectorsExercise2.Pair::getKey, toMap(Key::getId, Function.identity(), (x, y) -> x)),
//                                mapping(CollectorsExercise2.Pair::getValue, groupingBy(Value::getKeyId, toList()))
                                groupingBy(p -> p.getKey().getId(), mapping(CollectorsExercise2.Pair::getValue, toList()))
                        )
                );

        final Map<Key, List<Value>> result = pairMap.getB().entrySet().stream()
                .collect(
                        toMap(e -> pairMap.getA().get(e.getKey()), Map.Entry::getValue));

        assertThat(result.entrySet(), everyItem(isIn(expected.entrySet())));


    }

}
