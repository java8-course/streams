package part2.exercise;

import data.Employee;
import data.JobHistoryEntry;
import data.Person;
import org.junit.Assert;
import org.junit.Test;
import sun.invoke.empty.Empty;

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

public class CollectorsExercise2 {

    private static String generateString () {
        final String letters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        final int maxLength = 10;
        final int length = ThreadLocalRandom.current().nextInt(maxLength) + 1;

        return IntStream.range(0, length)
                .mapToObj(letters::charAt)
                .map(Object::toString)
                .collect(Collectors.joining());
    }

    private static String[] generateStringArray (int length) {
        return Stream.generate(CollectorsExercise2::generateString)
                .limit(length)
                .toArray(String[]::new);
    }

    public static String pickString (String[] array) {
        return array[ThreadLocalRandom.current().nextInt(array.length)];
    }

    public static class Key {
        private final String id;

        public Key (String id) {
            this.id = id;
        }

        public String getId () {
            return id;
        }

        @Override
        public boolean equals (Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            Key key = (Key) o;

            return id.equals(key.id);

        }

        @Override
        public int hashCode () {
            return id.hashCode();
        }
    }

    public static class Value {
        private final String keyId;

        public Value (String keyId) {
            this.keyId = keyId;
        }

        public String getKeyId () {
            return keyId;
        }
    }

    public static class Pair {
        private final Key key;
        private final Value value;

        public Pair (Key key, Value value) {
            this.key = key;
            this.value = value;
        }

        public Key getKey () {
            return key;
        }

        public Value getValue () {
            return value;
        }
    }

    public static List<Pair> generatePairs (int idCount, int length) {
        final String[] ids = generateStringArray(idCount);

        return Stream.generate(() -> new Pair(new Key(pickString(ids)), new Value(pickString(ids))))
                .limit(length)
                .collect(toList());
    }

    private static class SubResult {
        private final Map<Key, List<Value>> subResult;
        private final Map<String, List<Key>> knownKeys;
        private final Map<String, List<Value>> valuesWithoutKeys;

        public SubResult (Map<Key, List<Value>> subResult, Map<String, List<Key>> knownKeys, Map<String, List<Value>> valuesWithoutKeys) {
            this.subResult = subResult;
            this.knownKeys = knownKeys;
            this.valuesWithoutKeys = valuesWithoutKeys;
        }

        public Map<Key, List<Value>> getSubResult () {
            return subResult;
        }

        public Map<String, List<Value>> getValuesWithoutKeys () {
            return valuesWithoutKeys;
        }

        public Map<String, List<Key>> getKnownKeys () {
            return knownKeys;
        }
    }

    private static class MapPair {
        private final Map<String, Key> keyById;
        private final Map<String, Set<Value>> valueById;

        public MapPair () {
            this(new HashMap<>(), new HashMap<>());
        }

        public MapPair (Map<String, Key> keyById, Map<String, Set<Value>> valueById) {
            this.keyById = keyById;
            this.valueById = valueById;
        }

        public Map<String, Key> getKeyById () {
            return keyById;
        }

        public Map<String, Set<Value>> getValueById () {
            return valueById;
        }
    }

    private static <K, V, M extends Map<K, V>>
    BinaryOperator<M> mapMerger (BinaryOperator<V> mergeFunction) {
        return (m1, m2) -> {
            for (Map.Entry<K, V> e : m2.entrySet()) {
                m1.merge(e.getKey(), e.getValue(), mergeFunction);
            }
            return m1;
        };
    }

    @Test
    public void collectKeyValueMap () {
        final List<Pair> pairs = generatePairs(10, 100);

        // В два прохода
        // final Map<String, Key> keyMap1 = pairs.stream()...
        Map<String, Key> keyMap1 = pairs.stream().
                map(Pair::getKey)
                .collect(toMap(Key::getId, k -> k, (k1, k2) -> k1));

        // final Map<String, List<Value>> valuesMap1 = pairs.stream()...
        Map<String, Set<Value>> valuesMap1 = pairs.stream()
                .map(Pair::getValue)
                .collect(groupingBy(Value::getKeyId, Collectors.toSet()));

        // В каждом Map.Entry id ключа должно совпадать с keyId для каждого значения в списке
        // final Map<Key, List<Value>> keyValuesMap1 = valueMap1.entrySet().stream()...
        Map<Key, Set<Value>> keyValueMap1 = valuesMap1.entrySet().stream().collect(Collectors.toMap(entry -> keyMap1.get(entry.getKey()),
                Map.Entry::getValue, (values1, values2) -> {
                    values1.addAll(values2);
                    return values1;
                }));


        // В 1 проход в 2 Map с использованием MapPair и mapMerger
        final MapPair res2 = pairs.stream()
                .collect(new Collector<Pair, MapPair, MapPair>() {
                    @Override
                    public Supplier<MapPair> supplier () {
                        return MapPair::new;
                    }

                    @Override
                    public BiConsumer<MapPair, Pair> accumulator () {
                        return (mapPair, pair) -> {
                            mapPair.getKeyById().merge(pair.getKey().getId(), pair.getKey(), (k1, k2) -> k1);
                            mapPair.getValueById()
                                    .computeIfAbsent(pair.getValue().getKeyId(), (s) -> new HashSet<>())
                                    .add(pair.getValue());
                        };
                    }

                    @Override
                    public BinaryOperator<MapPair> combiner () {

                        return (mp1, mp2) -> {
                            BinaryOperator<Map<String, Key>> keyMapsMergeOperator = CollectorsExercise2.mapMerger(
                                    (Key key1, Key key2) -> key1);

                            Map<String, Key> keyMap = keyMapsMergeOperator.apply(
                                    mp1.getKeyById(),
                                    mp2.getKeyById());

                            BinaryOperator<Map<String, Set<Value>>> valuesMapsMergeOperator = CollectorsExercise2.mapMerger(
                                    (Set<Value> set1, Set<Value> set2) -> {
                                        set1.addAll(set2);
                                        return set1;
                                    });
                            Map<String, Set<Value>> valuesMap =
                                    valuesMapsMergeOperator.apply(mp1.getValueById(), mp2.getValueById());
                            return new MapPair(keyMap, valuesMap);
                        };
                    }

                    @Override
                    public Function<MapPair, MapPair> finisher () {
                        return Function.identity();
                    }

                    @Override
                    public Set<Characteristics> characteristics () {
                        return Collections.unmodifiableSet(EnumSet.of(
                                Characteristics.UNORDERED,
                                Characteristics.IDENTITY_FINISH));
                    }
                });

        final Map<String, Key> keyMap2 = res2.getKeyById();
        final Map<String, Set<Value>> valuesMap2 = res2.getValueById();

        final Map<Key, Set<Value>> keyValuesMap2 = valuesMap2.entrySet().stream().collect(Collectors.toMap(entry -> keyMap2.get(entry.getKey()),
                Map.Entry::getValue, (values1, values2) -> {
                    values1.addAll(values2);
                    return values1;
                }));

        Assert.assertEquals(keyValueMap1, keyValuesMap2);

        final Map<String, Key> expectedKeys = new HashMap<>();
        final Map<String, Set<Value>> expectedValues = new HashMap<>();

        for (Pair p : pairs) {
            expectedKeys.put(p.getKey().getId(), p.getKey());
            expectedValues.merge(p.getValue().getKeyId(), new HashSet<>(Collections.singletonList(p.getValue())),
                    (s1, s2) -> {
                        s1.addAll(s2);
                        return s1;
                    }
            );
        }

        final Map<Key, Set<Value>> expected = expectedValues.entrySet().stream().collect(Collectors.toMap(entry -> keyMap2.get(entry.getKey()),
                Map.Entry::getValue, (values1, values2) -> {
                    values1.addAll(values2);
                    return values1;
                }));

        Assert.assertEquals(keyValueMap1, expected);


        // Получение результата сразу:

//        final SubResult res3 = pairs.stream()
//                .collect(new Collector<Pair, SubResult, SubResult>() {
//                    @Override
//                    public Supplier<SubResult> supplier() {
//                        // TODO
//                        throw new UnsupportedOperationException();
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
//
//        // compare results
    }

}