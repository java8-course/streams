package part2.exercise;

import org.junit.Test;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static java.util.stream.Collectors.*;

public class CollectorsExercise2 {

    private static String generateString() {
        final String letters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        final int maxLength = 10;
        final int length = ThreadLocalRandom.current().nextInt(maxLength) + 1;

        return IntStream.range(0, length)
                .mapToObj(letters::charAt)
                .map(Object::toString)
                .collect(Collectors.joining());
    }

    private static String[] generateStringArray(final int length) {
        return Stream.generate(CollectorsExercise2::generateString)
                .limit(length)
                .toArray(String[]::new);
    }

    private static String pickString(final String[] array) {
        return array[ThreadLocalRandom.current().nextInt(array.length)];
    }

    public static class Key {
        private final String id;

        public Key(final String id) {
            this.id = id;
        }

        public String getId() {
            return id;
        }

        @Override
        public boolean equals(final Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            final Key key = (Key) o;

            return id.equals(key.id);
        }

        @Override
        public int hashCode() {
            return id.hashCode();
        }
    }

    public static class Value {
        private final String keyId;

        public Value(final String keyId) {
            this.keyId = keyId;
        }

        public String getKeyId() {
            return keyId;
        }
    }

    public static class Pair {
        private final Key key;
        private final Value value;

        public Pair(final Key key, final Value value) {
            this.key = key;
            this.value = value;
        }

        public Key getKey() {
            return key;
        }

        public Value getValue() {
            return value;
        }
    }

    public static List<Pair> generatePairs(final int idCount, final int length) {
        final String[] ids = generateStringArray(idCount);

        return Stream.generate(() -> new Pair(new Key(pickString(ids)), new Value(pickString(ids))))
                .limit(length)
                .collect(toList());
    }

    private static class SubResult {
        private final Map<Key, List<Value>> subResult;
        private final Map<String, List<Key>> knownKeys;
        private final Map<String, List<Value>> valuesWithoutKeys;

        public SubResult(final Map<Key, List<Value>> subResult, final Map<String, List<Key>> knownKeys, final Map<String, List<Value>> valuesWithoutKeys) {
            this.subResult = subResult;
            this.knownKeys = knownKeys;
            this.valuesWithoutKeys = valuesWithoutKeys;
        }

        public Map<Key, List<Value>> getSubResult() {
            return subResult;
        }

        public Map<String, List<Value>> getValuesWithoutKeys() {
            return valuesWithoutKeys;
        }

        public Map<String, List<Key>> getKnownKeys() {
            return knownKeys;
        }
    }

    private static class MapPair {
        private final Map<String, Key> keyById;
        private final Map<String, List<Value>> valueById;

        public MapPair() {
            this(new HashMap<>(), new HashMap<>());
        }

        public MapPair(final Map<String, Key> keyById, final Map<String, List<Value>> valueById) {
            this.keyById = keyById;
            this.valueById = valueById;
        }

        public Map<String, Key> getKeyById() {
            return keyById;
        }

        public Map<String, List<Value>> getValueById() {
            return valueById;
        }
    }

    private static <K, V, M extends Map<K, V>>
    BinaryOperator<M> mapMerger(final BinaryOperator<V> mergeFunction) {
        return (m1, m2) -> {
            for (final Map.Entry<K, V> e : m2.entrySet()) {
                m1.merge(e.getKey(), e.getValue(), mergeFunction);
            }
            return m1;
        };
    }

    @Test
    public void collectKeyValueMap() {
        final List<Pair> pairs = generatePairs(10, 100);

        // В два прохода
        // final Map<String, Key> keyMap1 = pairs.stream()...

        final Map<String, Key> idToKey = pairs.stream()
                .collect(
                        toMap(
                                pair -> pair.getKey().getId(),
                                Pair::getKey
                        )
                );

        // final Map<String, List<Value>> valuesMap1 = pairs.stream()...
        final Map<Key, List<Value>> result = pairs.stream()
                .collect(groupingBy(
                        pair -> pair.getValue().getKeyId(),
                        collectingAndThen(mapping(Pair::getValue, toList()), Function.identity())
                ))
                .entrySet().stream()
                .collect(
                        toMap(
                                pair -> idToKey.get(pair.getKey()),
                                Map.Entry::getValue,
                                (l1, l2) -> {
                                    l1.addAll(l2);
                                    return l1;
                                }
                        )
                );


        // В каждом Map.Entry id ключа должно совпадать с keyId для каждого значения в списке
        // final Map<Key, List<Value>> keyValuesMap1 = valueMap1.entrySet().stream()...

        // В 1 проход в 2 Map с использованием MapPair и mapMerger
        final MapPair res2 = pairs.stream()
                .collect(new Collector<Pair, MapPair, MapPair>() {
                    @Override
                    public Supplier<MapPair> supplier() {
                        return MapPair::new;
                    }

                    @Override
                    public BiConsumer<MapPair, Pair> accumulator() {
                        return (mapPair, pair) -> {
                            mapPair.getKeyById().put(pair.getKey().getId(),pair.getKey());
                            mapPair.getValueById().merge(
                                    pair.getValue().getKeyId(),
                                    Collections.singletonList(pair.getValue()),
                                    this::listMerger
                            );
                        };
                    }

                    private List<Value> listMerger(List<Value> values, List<Value> values1) {
                        values.addAll(values1);
                        return values;
                    }

                    @Override
                    public BinaryOperator<MapPair> combiner() {
                        /*return (m1, m2) -> {
                            mapMerger((Key k1, Key k2) -> k1.equals(k2) ? k1 : k2).apply(m1.getKeyById(),m2.getKeyById());
                        }*/
                        //return mapMerger(this::listMerger);
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
                });

        final Map<String, Key> keyMap2 = res2.getKeyById();
        final Map<String, List<Value>> valuesMap2 = res2.getValueById();

        // final Map<Key, List<Value>> keyValuesMap2 = valueMap2.entrySet().stream()...

        // Получение результата сразу:

        final SubResult res3 = pairs.stream()
                .collect(new Collector<Pair, SubResult, SubResult>() {
                    @Override
                    public Supplier<SubResult> supplier() {
                        // TODO
                        throw new UnsupportedOperationException();
                    }

                    @Override
                    public BiConsumer<SubResult, Pair> accumulator() {
                        // TODO add key to map, then check value.keyId and add it to one of maps
                        throw new UnsupportedOperationException();
                    }

                    @Override
                    public BinaryOperator<SubResult> combiner() {
                        // TODO use mapMerger, then check all valuesWithoutKeys
                        throw new UnsupportedOperationException();
                    }

                    @Override
                    public Function<SubResult, SubResult> finisher() {
                        // TODO use mapMerger, then check all valuesWithoutKeys
                        throw new UnsupportedOperationException();
                    }

                    @Override
                    public Set<Characteristics> characteristics() {
                        return Collections.unmodifiableSet(EnumSet.of(
                                Characteristics.UNORDERED));
                    }
                });

        final Map<Key, List<Value>> keyValuesMap3 = res3.getSubResult();

        // compare results
    }

}
