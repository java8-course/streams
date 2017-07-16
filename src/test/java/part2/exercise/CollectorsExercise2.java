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
import static org.junit.Assert.assertEquals;

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

    private static String[] generateStringArray(int length) {
        return Stream.generate(CollectorsExercise2::generateString)
                .limit(length)
                .toArray(String[]::new);
    }

    public static String pickString(String[] array) {
        return array[ThreadLocalRandom.current().nextInt(array.length)];
    }

    public static class Key {
        private final String id;

        public Key(String id) {
            this.id = id;
        }

        public String getId() {
            return id;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            Key key = (Key) o;

            return id.equals(key.id);

        }

        @Override
        public int hashCode() {
            return id.hashCode();
        }
    }

    public static class Value {
        private final String keyId;

        public Value(String keyId) {
            this.keyId = keyId;
        }

        public String getKeyId() {
            return keyId;
        }
    }

    public static class Pair {
        private final Key key;
        private final Value value;

        public Pair(Key key, Value value) {
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

    public static List<Pair> generatePairs(int idCount, int length) {
        final String[] ids = generateStringArray(idCount);

        return Stream.generate(() -> new Pair(new Key(pickString(ids)), new Value(pickString(ids))))
                .limit(length)
                .collect(toList());
    }

    private static class SubResult {
        private final Map<Key, List<Value>> subResult;
        private final Map<String, Key> knownKeys;
        private final Map<String, List<Value>> valuesWithoutKeys;

        public SubResult(Map<Key, List<Value>> subResult, Map<String, Key> knownKeys, Map<String,
                List<Value>> valuesWithoutKeys) {
            this.subResult = subResult;
            this.knownKeys = knownKeys;
            this.valuesWithoutKeys = valuesWithoutKeys;
        }

        public SubResult(Map<String, Key> knownKeys, Map<String, List<Value>> valuesWithoutKeys) {
            this(new HashMap<>(), knownKeys, valuesWithoutKeys);
        }

        public SubResult() {
            this(new HashMap<>(), new HashMap<>(), new HashMap<>());
        }

        public Map<Key, List<Value>> getSubResult() {
            return subResult;
        }

        public Map<String, List<Value>> getValuesWithoutKeys() {
            return valuesWithoutKeys;
        }

        public Map<String, Key> getKnownKeys() {
            return knownKeys;
        }
    }

    private static class MapPair {
        private final Map<String, Key> keyById;
        private final Map<String, List<Value>> valueById;

        public MapPair() {
            this(new HashMap<>(), new HashMap<>());
        }

        public MapPair(Map<String, Key> keyById, Map<String, List<Value>> valueById) {
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
    BinaryOperator<M> mapMerger(BinaryOperator<V> mergeFunction) {
        return (m1, m2) -> {
            for (Map.Entry<K, V> e : m2.entrySet()) {
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

        // final Map<String, List<Value>> valuesMap1 = pairs.stream()...

        // В каждом Map.Entry id ключа должно совпадать с keyId для каждого значения в списке
        // final Map<Key, List<Value>> keyValuesMap1 = valueMap1.entrySet().stream()...
        final Map<String, Key> keyMap1 =
                pairs.stream()
                        .collect(
                                toMap(
                                        o -> o.getKey().getId(),
                                        t -> t.getKey(),
                                        (u, u2) -> u
                                )
                        );

        final Map<String, List<Value>> valuesMap1 =
                pairs.stream()
                        .collect(
                                groupingBy(
                                        o -> o.getValue().getKeyId(),
                                        mapping(o -> o.getValue(), toList())
                                )
                        );

        Map<Key, List<Value>> firstResult = valuesMap1.entrySet()
                .stream()
                .collect(
                        toMap(
                                o -> keyMap1.get(o.getKey()),
                                o -> o.getValue()
                        )
                );

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
                            mapPair.getKeyById().putIfAbsent(pair.getKey().getId(), pair.getKey());
                            mapPair.getValueById().computeIfAbsent(pair.getValue().getKeyId(),
                                    s -> new ArrayList<>()).add(pair.getValue());
                        };
                    }

                    @Override
                    public BinaryOperator<MapPair> combiner() {
                        return (mapPair1, mapPair2) -> {
                            BinaryOperator<Map<String, Key>> keyBinaryOperator = mapMerger((o, o2) -> o);
                            Map<String, Key> mapKeys =
                                    keyBinaryOperator.apply(mapPair1.getKeyById(), mapPair2.getKeyById());

                            BinaryOperator<Map<String, List<Value>>> valueBinaryOperator =
                                    mapMerger(
                                            (list1, list2) -> {
                                                list1.addAll(list2);
                                                return list1;
                                            });
                            Map<String, List<Value>> mapValues =
                                    valueBinaryOperator.apply(mapPair1.getValueById(), mapPair2.getValueById());

                            return new MapPair(mapKeys, mapValues);
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
                                Collector.Characteristics.IDENTITY_FINISH));
                    }
                });

        final Map<String, Key> keyMap2 = res2.getKeyById();
        final Map<String, List<Value>> valuesMap2 = res2.getValueById();

//         final Map<Key, List<Value>> keyValuesMap2 = valueMap2.entrySet().stream()...
        final Map<Key, List<Value>> secondResult =
                valuesMap2.entrySet().stream()
                        .collect(
                                toMap(
                                        o -> keyMap2.get(o.getKey()),
                                        o -> o.getValue()
                                )
                        );

        // Получение результата сразу:
        final SubResult res3 = pairs.stream()
                .collect(new Collector<Pair, SubResult, SubResult>() {
                    @Override
                    public Supplier<SubResult> supplier() {
                        return SubResult::new;
                    }

                    @Override
                    public BiConsumer<SubResult, Pair> accumulator() {
                        return (subResult, pair) -> {
                            subResult.getKnownKeys().putIfAbsent(pair.getKey().getId(), pair.getKey());
                            subResult.getValuesWithoutKeys().computeIfAbsent(pair.getValue().getKeyId(),
                                    s -> new ArrayList<>()).add(pair.getValue());

                        };
                    }

                    @Override
                    public BinaryOperator<SubResult> combiner() {
                        return (subResult1, subResult2) -> {
                            BinaryOperator<Map<String, Key>> keyBinaryOperator = mapMerger((o, o2) -> o);
                            Map<String, Key> mapKeys =
                                    keyBinaryOperator.apply(subResult1.getKnownKeys(), subResult2.getKnownKeys());

                            BinaryOperator<Map<String, List<Value>>> valueBinaryOperator =
                                    mapMerger(
                                            (list1, list2) -> {
                                                list1.addAll(list2);
                                                return list1;
                                            });
                            Map<String, List<Value>> mapValues =
                                    valueBinaryOperator.apply(
                                            subResult1.getValuesWithoutKeys(),
                                            subResult2.getValuesWithoutKeys()
                                    );

                            return new SubResult(mapKeys, mapValues);
                        };
                    }

                    @Override
                    public Function<SubResult, SubResult> finisher() {
                        return subResult -> {
                            Map<Key, List<Value>> collect = subResult.getValuesWithoutKeys().entrySet()
                                    .stream()
                                    .collect(
                                            toMap(
                                                    t -> subResult.getKnownKeys().get(t.getKey()),
                                                    o -> o.getValue()
                                            )
                                    );

                            return new SubResult(collect, subResult.knownKeys, subResult.getValuesWithoutKeys());
                        };
                    }

                    @Override
                    public Set<Characteristics> characteristics() {
                        return Collections.unmodifiableSet(EnumSet.of(
                                Characteristics.UNORDERED));
                    }
                });

        final Map<Key, List<Value>> thirdResult = res3.getSubResult();

        // compare results
        firstResult.forEach((key, values) -> {
            List<Value> secondValues = secondResult.get(key);
            assertEquals(values.size(), secondValues.size());
            assertEquals(key.getId(), values.get(0).getKeyId());
        });

        secondResult.forEach((key, values) -> {
            List<Value> thirdValues = thirdResult.get(key);
            assertEquals(values.size(), thirdValues.size());
            assertEquals(key.getId(), values.get(0).getKeyId());
        });

        thirdResult.forEach((key, values) -> {
            List<Value> firstValues = firstResult.get(key);
            assertEquals(values.size(), firstValues.size());
            assertEquals(key.getId(), values.get(0).getKeyId());
        });
    }

}
