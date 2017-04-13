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

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;

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

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            Value value = (Value) o;

            return keyId != null ? keyId.equals(value.keyId) : value.keyId == null;
        }

        @Override
        public int hashCode() {
            return keyId != null ? keyId.hashCode() : 0;
        }

        @Override
        public String toString() {
            return "Value{" +
                    "keyId='" + keyId + '\'' +
                    '}';
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
        private final Map<String, List<Key>> knownKeys;
        private final Map<String, List<Value>> valuesWithoutKeys;

        public SubResult(Map<Key, List<Value>> subResult, Map<String, List<Key>> knownKeys, Map<String, List<Value>> valuesWithoutKeys) {
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
        final Map<String, Key> keyMap1 = pairs.stream()
                .map(Pair::getKey)
                .collect(toMap(Key::getId, Function.identity(), (k1, k2) -> k2));

        final Map<String, List<Value>> valueMap1 = pairs.stream()
                .map(Pair::getValue)
                .collect(groupingBy(Value::getKeyId));

        // В каждом Map.Entry id ключа должно совпадать с keyId для каждого значения в списке
        final Map<Key, List<Value>> keyValuesMap1 = valueMap1.entrySet().stream()
                .collect(toMap(e -> keyMap1.get(e.getKey()),
                        Map.Entry::getValue,
                        (values, values2) -> {
                            values.addAll(values2);
                            return values;
                        }));

        // В 1 проход в 2 Map с использованием MapPair и mapMerger
        final MapPair res2 = pairs.stream()
                .collect(new Collector<Pair, MapPair, MapPair>() {
                    @Override
                    public Supplier<MapPair> supplier() {
                        return MapPair::new;
                    }

                    @Override
                    public BiConsumer<MapPair, Pair> accumulator() {
                        return (map, pair) -> {
                            map.getKeyById().merge(pair.getKey().getId(), pair.getKey(), (key, key2) -> key);
                            map.getValueById()
                                    .computeIfAbsent(pair.getValue().getKeyId(), (t) -> new ArrayList<>())
                                    .add(pair.getValue());
                        };
                    }

                    @Override
                    public BinaryOperator<MapPair> combiner() {
                        return (map1, map2) -> {
                            BinaryOperator<Map<String, Key>> mapBinaryOperator = mapMerger((o, o2) -> o);
                            BinaryOperator<Map<String, List<Value>>> mapBinaryOperatorToo = mapMerger((list, list2) -> {
                                list.addAll(list2);
                                return list;
                            });

                            Map<String, Key> keyMap =
                                    mapBinaryOperator.apply(map1.getKeyById(), map2.getKeyById());
                            Map<String, List<Value>> valueMap =
                                    mapBinaryOperatorToo.apply(map1.getValueById(), map2.getValueById());

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
                });

        final Map<String, Key> keyMap2 = res2.getKeyById();
        final Map<String, List<Value>> valuesMap2 = res2.getValueById();

        final Map<Key, List<Value>> keyValuesMap2 = valuesMap2
                .entrySet().stream()
                .collect(toMap(e->keyMap2.get(e.getKey()),
                        Map.Entry::getValue,
                        (values, values2) -> {
                            values.addAll(values2);
                            return values;
                        }));
        assertThat(keyMap1, equalTo(keyMap2));
        assertThat(keyValuesMap1.keySet(), equalTo(keyValuesMap2.keySet()));

        Key randomKey = keyMap1.entrySet().stream()
                .map(Map.Entry::getValue)
                .findAny()
                .orElse(null);
        assertThat(keyValuesMap1.get(randomKey), equalTo(keyValuesMap2.get(randomKey)));

        // Получение результата сразу:

//        final SubResult res3 = pairs.stream()
//                .collect(new Collector<Pair, SubResult, SubResult>() {
//                    @Override
//                    public Supplier<SubResult> supplier() {
//                        return SubResult::new;
//                    }
//
//                    @Override
//                    public BiConsumer<SubResult, Pair> accumulator() {
//                        // TODO add key to map, then check value.keyId and add it to one of maps
//                        throw new UnsupportedOperationException();
//                    }
//
//                    @Override
//                    public BinaryOperator<SubResult> combiner() {
//                        // TODO use mapMerger, then check all valuesWithoutKeys
//                        throw new UnsupportedOperationException();
//                    }
//
//                    @Override
//                    public Function<SubResult, SubResult> finisher() {
//                        // TODO use mapMerger, then check all valuesWithoutKeys
//                        throw new UnsupportedOperationException();
//                    }
//
//                    @Override
//                    public Set<Characteristics> characteristics() {
//                        return Collections.unmodifiableSet(EnumSet.of(
//                                Characteristics.UNORDERED));
//                    }
//                });
//
//        final Map<Key, List<Value>> keyValuesMap3 = res3.getSubResult();

        // compare results
    }

}
