package part2.exercise;

import data.Employee;
import data.JobHistoryEntry;
import data.Person;
import org.junit.Assert;
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

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;
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
                .collect(toMap(Key::getId, Function.identity(), (v1, v2) -> v1));

        final Map<String, List<Value>> valuesMap1 = pairs.stream()
                .map(Pair::getValue)
                .collect(toMap(Value::getKeyId, Collections::singletonList,
                        (v1, v2) -> {
                            List<Value> result = new ArrayList<>();
                            result.addAll(v1);
                            result.addAll(v2);
                            return result;
                        }));

        final Map<Key, List<Value>> keyValuesMap1 = getResultMap(keyMap1, valuesMap1);

        // В 1 проход в 2 Map с использованием MapPair и mapMerger
        final MapPair res2 = pairs.stream()
                .collect(new KeyValueMappingCollector());

        final Map<Key, List<Value>> keyValuesMap2 = getResultMap(res2.getKeyById(), res2.getValueById());

        keyValuesMap2.forEach((k, v) -> {
            System.out.println(k.getId() + " -> e" + v.stream().map(Value::getKeyId).collect(toList()));
        });

        assertEquals(keyValuesMap1, keyValuesMap2);
    }

    public static Map<Key, List<Value>> getResultMap(Map<String, Key> keyMap1, Map<String, List<Value>> valuesMap1) {
        return valuesMap1.entrySet()
                        .stream()
                        .collect(toMap(ent -> keyMap1.get(ent.getKey()), Map.Entry::getValue));
    }

    public static class KeyValueMappingCollector implements Collector<Pair, MapPair, MapPair> {

        @Override
        public Supplier<MapPair> supplier() {
            return MapPair::new;
        }

        @Override
        public BiConsumer<MapPair, Pair> accumulator() {
            return (maps, pair) -> {
                maps.getKeyById().put(pair.getKey().getId(), pair.getKey());
                maps.getValueById().merge(pair.getValue().getKeyId(), Collections.singletonList(pair.getValue()), (v1, v2) -> {
                    List<Value> result = new ArrayList<>();
                    result.addAll(v1);
                    result.addAll(v2);
                    return result;
                });
            };
        }

        @Override
        public BinaryOperator<MapPair> combiner() {
            return (maps1, maps2) -> {
                BinaryOperator<Map<String, List<Value>>> mergeValues = mapMerger((v1, v2) -> {
                    v1.addAll(v2);
                    return v2;
                });

                Map<String, Key> keys = maps1.getKeyById();
                keys.putAll(maps2.getKeyById());
                Map<String, List<Value>> values = mergeValues.apply(maps1.getValueById(), maps2.getValueById());
                return new MapPair(keys, values);
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

}
