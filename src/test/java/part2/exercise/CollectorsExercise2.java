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
import static org.hamcrest.Matchers.isIn;
import static org.hamcrest.core.Every.everyItem;
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

    private static Map<Key, List<Value>> collectKeyValueMapWithoutStream(List<Pair> pairs) {
        final Map<Key, List<Value>> map = new HashMap<>();

        for (Pair pair : pairs) {
            final Key key = pair.getKey();
            final Value value = pair.getValue();
            if (!map.containsKey(key))
                map.put(key, new ArrayList<>());
            map.get(key).add(value);
        }

        return map;
    }

    private static Map<Key, List<Value>> collectKeyValueMapDoubleIteration(List<Pair> pairs) {

        final Map<String, Key> keyMap =
                pairs.stream()
                .collect(
                        toMap(p -> p.getKey().getId(), Pair::getKey, (a, b) -> a));

        final Map<String, List<Value>> valuesMap = pairs.stream()
                .collect(
                        groupingBy(p -> p.getKey().getId(), mapping(Pair::getValue, toList()))
                );

        return valuesMap.entrySet().stream()
                .collect(
                        toMap(e -> keyMap.get(e.getKey()), Map.Entry::getValue));

    }

    private static class MapPairCollector implements Collector<Pair, MapPair, MapPair> {

        @Override
        public Supplier<MapPair> supplier() {
            return MapPair::new;
        }

        @Override
        public BiConsumer<MapPair, Pair> accumulator() {
            return (m, p) -> {
                final Key key = p.getKey();
                final String sKey = key.getId();
                final Value value = p.getValue();
                final Map<String, Key> keyMap = m.getKeyById();
                final Map<String, List<Value>> valueMap = m.getValueById();
                keyMap.put(sKey, key);
                if (!valueMap.containsKey(sKey))
                    valueMap.put(sKey, new ArrayList<>());
                valueMap.get(sKey).add(value);
            };
        }

        @Override
        public BinaryOperator<MapPair> combiner() {
            return (a, b) -> {
                final BinaryOperator<Map<String, Key>> keyMerger = mapMerger((k1, k2) -> k1);
                final BinaryOperator<Map<String, List<Value>>> valueMerger = mapMerger((v1, v2) -> { v1.addAll(v2); return v1; });
                return new MapPair(
                        keyMerger.apply(a.getKeyById(), b.getKeyById()),
                        valueMerger.apply(a.getValueById(), b.getValueById()));
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

    public static Map<Key, List<Value>> collectKeyValueMapPair(List<Pair> pairs) {

        final MapPair mapPair = pairs.stream().collect(new MapPairCollector());
        final Map<String, Key> keyMap = mapPair.getKeyById();
        final Map<String, List<Value>> valuesMap = mapPair.getValueById();

        return valuesMap.entrySet().stream()
                .collect(
                        toMap(e -> keyMap.get(e.getKey()), Map.Entry::getValue));

    }

    @Test
    public void collectKeyValueMap() {
        final List<Pair> pairs = generatePairs(10, 100);
        final Map<Key, List<Value>> expectedWithoutStream = collectKeyValueMapWithoutStream(pairs);

        assertThat(collectKeyValueMapDoubleIteration(pairs).entrySet(), everyItem(isIn(expectedWithoutStream.entrySet())));
        assertThat(collectKeyValueMapPair(pairs).entrySet(), everyItem(isIn(expectedWithoutStream.entrySet())));

    }

}
