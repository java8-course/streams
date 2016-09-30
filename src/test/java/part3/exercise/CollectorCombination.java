package part3.exercise;

import org.junit.Test;
import part2.exercise.CollectorsExercise2;
import part2.exercise.CollectorsExercise2.Key;
import part2.exercise.CollectorsExercise2.Value;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
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
        // TODO
        throw new UnsupportedOperationException();
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


        // TODO tests
        throw new UnsupportedOperationException();
    }

}
