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
        private final Map<Key, Set<Value>> subResult;
        private final Map<String, List<Key>> knownKeys;
        private final Map<String, List<Value>> valuesWithoutKeys;

        public SubResult(Map<Key, Set<Value>> subResult, Map<String, List<Key>> knownKeys, Map<String, List<Value>> valuesWithoutKeys) {
            this.subResult = subResult;
            this.knownKeys = knownKeys;
            this.valuesWithoutKeys = valuesWithoutKeys;
        }

        public Map<Key, Set<Value>> getSubResult() {
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
            keyById = new HashMap<>();
            valueById = new HashMap<>();
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
            for (Map.Entry<K, V> e : m2.entrySet())
                m1.merge(e.getKey(), e.getValue(), mergeFunction);
            return m1;
        };
    }

    Map<Key, Set<Value>> collectKeysValuesMode0(List<Pair> pairs, Set<Value> orphansOutput) {
        final Map<String, Key> keyMap1 = new HashMap<>();
        final Map<Key, Set<Value>> result = new HashMap<>();
        pairs.forEach(pair -> {
            final Key key = pair.getKey();
            keyMap1.put(key.getId(), key);
            result.put(key, new HashSet<>());
        });

        orphansOutput.clear();

        pairs.forEach(pair -> {
            final Value value = pair.getValue();
            String keyId = value.getKeyId();
            if (keyMap1.containsKey(keyId))
                result.get(keyMap1.get(keyId)).add(value);
            else
                orphansOutput.add(value);
        });
        return result;
    }

    Map<Key, Set<Value>> collectKeysValuesMode1(List<Pair> pairs, Set<Value> orphansOutput) {
        final Map<String, Key> keyMap1 = pairs.stream()
                .map(Pair::getKey)
                .collect(toMap(Key::getId, Function.identity(), (k1, k2) -> k1));

        orphansOutput.clear();

        final Map<Key, Set<Value>> result = pairs.stream()
                .map(Pair::getValue)
                .collect(groupingBy(value -> keyMap1.get(value.getKeyId()), toSet()));

        Set<Value> orphans = result.remove(null);
        return result;
    }


    @Test
    public void collectKeyValueMap() {
        final List<Pair> pairs = generatePairs(10, 100);

        Set<Value> orph0 = new HashSet<>();
        final Map<Key, Set<Value>> res0 = collectKeysValuesMode0(pairs, orph0);
        Set<Value> orph1 = new HashSet<>();
        final Map<Key, Set<Value>> res1 = collectKeysValuesMode1(pairs, orph1);
        assertEquals(res0, res1);
        assertEquals(orph0, orph1);

        // В два прохода
        // final Map<String, Key> keyMap1 = pairs.stream()...

        // final Map<String, List<Value>> valuesMap1 = pairs.stream()...

        // В каждом Map.Entry id ключа должно совпадать с keyId для каждого значения в списке11
        // final Map<Key, Set<Value>> keyValuesMap1 = valueMap1.entrySet().stream()...

        // В 1 проход в 2 Map с использованием MapPair и mapMerger
        final MapPair res2 = pairs.stream()
                .collect(new Collector<Pair, MapPair, MapPair>() {
                    @Override
                    public Supplier<MapPair> supplier() {
                        return MapPair::new;
                    }

                    @Override
                    public BiConsumer<MapPair, Pair> accumulator() {
                        return (mp, p) -> {
                            // TODO add key and value to maps
                        };
                    }

                    @Override
                    public BinaryOperator<MapPair> combiner() {
                        // TODO use mapMerger
                        throw new UnsupportedOperationException();
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

        // final Map<Key, Set<Value>> keyValuesMap2 = valueMap2.entrySet().stream()...
    }
/*
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

        final Map<Key, Set<Value>> keyValuesMap3 = res3.getSubResult();

        // compare results
    }

    }

}
