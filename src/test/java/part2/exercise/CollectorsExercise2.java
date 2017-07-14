package part2.exercise;

import org.junit.Test;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;
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
                .collect(joining());
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

    public static Set<Pair> generatePairs(int idCount, int length) {
        final String[] ids = generateStringArray(idCount);

        return Stream.generate(() -> new Pair(new Key(pickString(ids)), new Value(pickString(ids))))
                .limit(length)
                .collect(toSet());
    }

    private static class SubResult {
        private final Map<Key, List<Value>> subResult;
        private final Map<String, Key> knownKeys;
        private final Map<String, List<Value>> valuesWithoutKeys;

        public SubResult(Map<Key, List<Value>> subResult) {
            this.subResult = subResult;
            this.knownKeys = null;
            this.valuesWithoutKeys = null;
        }

        public SubResult(Map<Key, List<Value>> subResult, Map<String, Key> knownKeys, Map<String, List<Value>> valuesWithoutKeys) {
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

        public Map<String, Key> getKnownKeys() {
            return knownKeys;
        }
    }

    private static class MapPair {
        private final Map<String, Key> keyById;
        private final Map<String, List<Value>> valueById;

        MapPair() {
            this(new HashMap<>(), new HashMap<>());
        }

        MapPair(Map<String, Key> keyById, Map<String, List<Value>> valueById) {
            this.keyById = keyById;
            this.valueById = valueById;
        }

        Map<String, Key> getKeyById() {
            return keyById;
        }

        Map<String, List<Value>> getValueById() {
            return valueById;
        }

        void put(Pair p) {
            keyById.put(p.getKey().getId(), p.getKey());
            final ArrayList<Value> value = new ArrayList<>();
            value.add(p.getValue());
            valueById.merge(p.getValue().getKeyId(),
                    value,
                    (l1, l2) -> {
                        l1.addAll(l2);
                        return l1;
                    });
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
        final Set<Pair> pairs = generatePairs(10, 100);

        // В два прохода
        final Map<String, Key> keyMap1 = pairs.stream()
                .collect(toMap(o -> o.getKey().getId(), Pair::getKey, (k1, k2) -> k1));

        final Map<String, List<Value>> valuesMap1 = pairs.stream()
                .collect(toMap(
                        o -> o.getValue().getKeyId(),
                        t -> {
                            final ArrayList<Value> values = new ArrayList<>();
                            values.add(t.getValue());
                            return values;
                        },
                        (l1, l2) -> {
                            l1.addAll(l2);
                            return l1;
                        }
                ));

        // В каждом Map.Entry id ключа должно совпадать с keyId для каждого значения в списке
        final Map<Key, List<Value>> keyValuesMap1 = valuesMap1.entrySet().stream()
                .collect(toMap(o -> keyMap1.get(o.getKey()), Map.Entry::getValue));

        // В 1 проход в 2 Map с использованием MapPair и mapMerger
        final MapPair res2 = pairs.stream()
                .collect(new Collector<Pair, MapPair, MapPair>() {
                    @Override
                    public Supplier<MapPair> supplier() {
                        // TODO
                        return MapPair::new;
                    }

                    @Override
                    public BiConsumer<MapPair, Pair> accumulator() {
                        // TODO add key and value to maps
                        return MapPair::put;
                    }

                    @Override
                    public BinaryOperator<MapPair> combiner() {
                        // TODO use mapMerger
                        return (mapPair, mapPair2) -> {
                            BinaryOperator<Map<String, Key>> keyMerger = mapMerger((v1, v2) -> v1);
                            keyMerger.apply(mapPair.getKeyById(), mapPair2.getKeyById());
                            BinaryOperator<Map<String, List<Value>>> valueMerger =
                                    mapMerger((v1, v2) -> {
                                        v1.addAll(v2);
                                        return v1;
                                    });
                            valueMerger.apply(mapPair.getValueById(), mapPair2.getValueById());
                            return mapPair;
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
                });

        final Map<String, Key> keyMap2 = res2.getKeyById();
        final Map<String, List<Value>> valuesMap2 = res2.getValueById();

        final Map<Key, List<Value>> keyValuesMap2 = valuesMap1.entrySet().stream()
                .collect(toMap(o -> keyMap1.get(o.getKey()), Map.Entry::getValue));

        // Получение результата сразу:

        final SubResult res3 = pairs.stream()
                .collect(new Collector<Pair, SubResult, SubResult>() {
                    @Override
                    public Supplier<SubResult> supplier() {
                        // TODO
                        return () -> new SubResult(new HashMap<>(), new HashMap<>(), new HashMap<>());
                    }

                    @Override
                    public BiConsumer<SubResult, Pair> accumulator() {
                        // TODO add key to map, then check value.keyId and add it to one of maps
                        return (subResult, pair) -> {
                            subResult.getKnownKeys().put(
                                    pair.getKey().getId(),
                                    pair.getKey());
                            final Map<String, List<Value>> values =
                                    subResult.getValuesWithoutKeys();
                            if (values.containsKey(pair.getValue().getKeyId()))
                                values.get(pair.getValue().getKeyId())
                                        .add(pair.getValue());
                            else {
                                List<Value> value = new ArrayList<>();
                                value.add(pair.getValue());
                                values.put(
                                        pair.getValue().getKeyId(),
                                        value);
                            }
                        };
                    }

                    @Override
                    public BinaryOperator<SubResult> combiner() {
                        // TODO use mapMerger, then check all valuesWithoutKeys
                        return (subResult1, subResult2) -> {
                            BinaryOperator<Map<String, Key>> keyMerger = mapMerger((v1, v2) -> v1);
                            keyMerger.apply(subResult1.getKnownKeys(), subResult2.getKnownKeys());
                            BinaryOperator<Map<String, List<Value>>> valueMerger =
                                    mapMerger((v1, v2) -> {
                                        v1.addAll(v2);
                                        return v1;
                                    });
                            valueMerger.apply(subResult1.getValuesWithoutKeys(), subResult2.getValuesWithoutKeys());
                            return subResult1;
                        };
                    }

                    @Override
                    public Function<SubResult, SubResult> finisher() {
                        // TODO use mapMerger, then check all valuesWithoutKeys
                        final Function<SubResult, Map<Key, List<Value>>> getResult =
                                subResult -> subResult.getValuesWithoutKeys().entrySet().stream()
                                .collect(toMap(o -> subResult.getKnownKeys().get(o.getKey()), Map.Entry::getValue));
                        return subResult -> new SubResult(getResult.apply(subResult));
                    }

                    @Override
                    public Set<Characteristics> characteristics() {
                        return Collections.unmodifiableSet(EnumSet.of(
                                Characteristics.UNORDERED));
                    }
                });

        final Map<Key, List<Value>> keyValuesMap3 = res3.getSubResult();

        assertEquals(keyValuesMap1, keyValuesMap2);
        assertEquals(keyValuesMap1, keyValuesMap3);

        // compare results
    }

}
