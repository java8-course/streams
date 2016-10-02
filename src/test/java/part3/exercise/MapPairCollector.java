package part3.exercise;

import part2.exercise.CollectorsExercise2.Key;
import part2.exercise.CollectorsExercise2.MapPair;
import part2.exercise.CollectorsExercise2.Pair;
import part2.exercise.CollectorsExercise2.Value;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;

import static part2.exercise.CollectorsExercise2.mapMerger;

public class MapPairCollector implements Collector<Pair, MapPair, MapPair> {

    @Override
    public Supplier<MapPair> supplier() {
        return MapPair::new;
    }

    @Override
    public BiConsumer<MapPair, Pair> accumulator() {
        return (MapPair mapPair, Pair pair) -> {
            mapPair.getKeyById().put(pair.getKey().getId(), pair.getKey());
            mapPair.getValueById()
                    .merge(
                            pair.getValue().getKeyId(),
                            new HashSet<>(Collections.singletonList(pair.getValue())),
                            (values, values2) -> {
                                values.addAll(values2);
                                return values;
                            });
        };
    }

    @Override
    public BinaryOperator<MapPair> combiner() {
        return (mapPair, mapPair2) -> {
            BinaryOperator<Map<String, Key>> keyMerger = mapMerger((key, key2) -> key);
            Map<String, Key> keyMap = keyMerger.apply(mapPair.getKeyById(), mapPair2.getKeyById());

            BinaryOperator<Map<String, Set<Value>>> valueMerger = mapMerger((values, values2) -> {
                values.addAll(values2);
                return values;
            });
            Map<String, Set<Value>> valueMap = valueMerger.apply(mapPair.getValueById(), mapPair2.getValueById());

            return new MapPair(keyMap, valueMap);
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
                Characteristics.IDENTITY_FINISH));
    }
}
