package part2.exercise;

import org.junit.Test;
import part3.exercise.MapPairCollector;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.BinaryOperator;
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

    public static class MapPair {
        private final Map<String, Key> keyById;
        private final Map<String, Set<Value>> valueById;

        public MapPair() {
            this(new HashMap<>(), new HashMap<>());
        }

        public MapPair(Map<String, Key> keyById, Map<String, Set<Value>> valueById) {
            this.keyById = keyById;
            this.valueById = valueById;
        }

        public Map<String, Key> getKeyById() {
            return keyById;
        }

        public Map<String, Set<Value>> getValueById() {
            return valueById;
        }
    }

    public static <K, V, M extends Map<K, V>>
    BinaryOperator<M> mapMerger(BinaryOperator<V> mergeFunction) {
        return (m1, m2) -> {
            for (Map.Entry<K, V> e : m2.entrySet())
                m1.merge(e.getKey(), e.getValue(), mergeFunction);
            return m1;
        };
    }

    @Test
    public void collectKeyValueMap() {
        final List<Pair> pairs = generatePairs(10, 100);

        // В два прохода
        final Map<String, Key> keyMap1 = pairs.stream()
                .collect(
                        toMap(
                                p -> p.getKey().getId(),
                                Pair::getKey,
                                (key, key2) -> key)
                );

        final Map<String, Set<Value>> valuesMap1 = pairs.stream()
                .collect(
                        groupingBy(
                                p -> p.getValue().getKeyId(),
                                mapping(Pair::getValue, toSet())
                        )
                );

        // В каждом Map.Entry id ключа должно совпадать с keyId для каждого значения в списке
        final Map<Key, Set<Value>> keyValuesMap1 = valuesMap1.entrySet().stream()
                .filter(entry -> keyMap1.containsKey(entry.getKey()))
                .collect(
                        toMap(
                                entry -> keyMap1.get(entry.getKey()),
                                Map.Entry::getValue)
                );

        // В 1 проход в 2 Map с использованием MapPair и mapMerger
        final MapPair res2 = pairs.stream()
                .collect(new MapPairCollector());
        final Map<String, Key> keyMap2 = res2.getKeyById();
        final Map<String, Set<Value>> valuesMap2 = res2.getValueById();

        final Map<Key, Set<Value>> keyValuesMap2 = valuesMap2.entrySet().stream()
                .filter(entry -> keyMap2.containsKey(entry.getKey()))
                .collect(
                        toMap(
                                entry -> keyMap2.get(entry.getKey()),
                                Map.Entry::getValue)
                );

        assertEquals(keyValuesMap1, keyValuesMap2);
    }
}
