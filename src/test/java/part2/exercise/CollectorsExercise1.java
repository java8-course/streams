package part2.exercise;

import data.Employee;
import data.JobHistoryEntry;
import data.Person;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.junit.Test;

import java.util.*;
import java.util.function.*;
import java.util.stream.Collector;
import java.util.stream.Stream;

import static java.util.stream.Collectors.*;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class CollectorsExercise1 {

    @Test
    public void getTheCoolestOne() {
        Map<String, Person> coolestByPosition = getCoolestByPosition(getEmployees());

        final Person coolestDev = new Person("John", "Doe", 30);
        assertThat(coolestByPosition.get("dev"), is(coolestDev));

        coolestByPosition = getCoolestByPositionWithCustomCollector(getEmployees());
        assertThat(coolestByPosition.get("dev"), is(coolestDev));
        coolestByPosition.forEach((position, person) -> System.out.println(position + " -> " + person));
    }

    @AllArgsConstructor
    @Getter
    private static class PersonPositionDuration {
        private final Person person;
        private final String position;
        private final int duration;
    }

    // With the longest duration on single job
    private Map<String, Person> getCoolestByPosition(List<Employee> employees) {

        //noinspection OptionalGetWithoutIsPresent
        final Map<String, Person> coolestByPosition1 = employees.stream()
                .flatMap(e -> e.getJobHistory().stream()
                        .map(jhe -> new PersonPositionDuration(e.getPerson(), jhe.getPosition(), jhe.getDuration())))
                .collect(groupingBy(PersonPositionDuration::getPosition,
                        collectingAndThen(
                                maxBy(Comparator.comparing(PersonPositionDuration::getDuration)),
                                ppd -> ppd.get().getPerson())));

        final Map<String, Person> coolestByPosition2 = employees.stream()
                .flatMap(e -> e.getJobHistory().stream()
                        .map(jhe -> new PersonPositionDuration(e.getPerson(), jhe.getPosition(), jhe.getDuration())))
                .collect(toMap(PersonPositionDuration::getPosition,
                        Function.identity(),
                        BinaryOperator.maxBy(Comparator.comparing(PersonPositionDuration::getDuration))))
                .entrySet().stream().collect(toMap(Map.Entry::getKey,
                        entry -> entry.getValue().getPerson()));

        return coolestByPosition2;
    }

    private static class CollectCoolest implements Collector<PersonPositionDuration, Map<String, PersonPositionDuration>, Map<String, Person>> {
        @Getter
        private static final Collector<PersonPositionDuration, Map<String, PersonPositionDuration>, Map<String, Person>> instance = new CollectCoolest();

        private final BiConsumer<Map<String, PersonPositionDuration>, PersonPositionDuration> accumulator = (stringPersonMap, personPositionDuration) -> {
            putIfCooler(stringPersonMap, personPositionDuration.getPosition(), personPositionDuration);
        };

        private final BinaryOperator<Map<String, PersonPositionDuration>> combiner = (map1, map2) -> {
            map2.entrySet().forEach(kvp -> putIfCooler(map1, kvp.getKey(), kvp.getValue()));
            return map1;
        };

        private CollectCoolest() {
        }

        private void putIfCooler(Map<String, PersonPositionDuration> map, String key, PersonPositionDuration ppd) {
            if (!map.containsKey(key) || map.get(key).getDuration() < ppd.getDuration())
                map.put(key, ppd);
        }

        @Override
        public Supplier<Map<String, PersonPositionDuration>> supplier() {
            return HashMap::new;
        }

        @Override
        public BiConsumer<Map<String, PersonPositionDuration>, PersonPositionDuration> accumulator() {
            return accumulator;
        }

        @Override
        public BinaryOperator<Map<String, PersonPositionDuration>> combiner() {
            return combiner;
        }

        @Override
        public Function<Map<String, PersonPositionDuration>, Map<String, Person>> finisher() {
            return spdmap -> spdmap.keySet().stream().collect(toMap(Function.identity(), key -> spdmap.get(key).getPerson()));
        }

        @Override
        public Set<Characteristics> characteristics() {
            return Collections.unmodifiableSet(EnumSet.of(
                    Characteristics.UNORDERED));
        }
    }

    @Test
    public void getTheCoolestOne2() {
        Map<String, Person> coolestByPosition = getCoolestByPosition2(getEmployees());

        coolestByPosition.forEach((position, person) -> System.out.println(position + " -> " + person));

        final Person coolestDev = new Person("John", "Galt", 23);
        assertThat(coolestByPosition.get("dev"), is(coolestDev));
        coolestByPosition = getCoolestByPosition2WithCustomCollector(getEmployees());
        assertThat(coolestByPosition.get("dev"), is(coolestDev));
    }


    // With the longest sum duration on this position
    // { John Doe, [{dev, google, 4}, {dev, epam, 4}] } предпочтительнее, чем { A B, [{dev, google, 6}, {QA, epam, 100}]}
    private Map<String, Person> getCoolestByPosition2(List<Employee> employees) {
        //noinspection OptionalGetWithoutIsPresent
        return employees.stream()
                .flatMap(e -> e.getJobHistory().stream()
                        .collect(groupingBy(JobHistoryEntry::getPosition,
                                summingInt(JobHistoryEntry::getDuration)))
                        .entrySet().stream()
                        .map(posDurEntry -> new PersonPositionDuration(e.getPerson(), posDurEntry.getKey(), posDurEntry.getValue())))
                .collect(groupingBy(PersonPositionDuration::getPosition,
                        collectingAndThen(
                                maxBy(Comparator.comparing(PersonPositionDuration::getDuration)),
                                ppd -> ppd.get().getPerson())));
    }

    private static class JobHistoryCombiner implements Collector<JobHistoryEntry, Map<String, int[]>, Stream<PersonPositionDuration>> {
        private static final IntBinaryOperator SUMMING_COMBINER = (i1, i2) -> i1 + i2;

        private final IntBinaryOperator durationCombiner;

        private JobHistoryCombiner(Person person, IntBinaryOperator durationCombiner) {
            finisher = m -> m.entrySet().stream()
                    .map(entry -> new PersonPositionDuration(person, entry.getKey(), entry.getValue()[0]));
            this.durationCombiner = durationCombiner;
        }

        private void putOrCombine(Map<String, int[]> map, String key, int value) {
            if (map.containsKey(key)) {
                final int[] v = map.get(key);
                v[0] = durationCombiner.applyAsInt(v[0], value);
            } else {
                int[] newV = {value};
                map.put(key, newV);
            }
        }

        private final BiConsumer<Map<String, int[]>, JobHistoryEntry> accumulator = (map, jhe) -> {
            String pos = jhe.getPosition();
            putOrCombine(map, pos, jhe.getDuration());
        };

        private final BinaryOperator<Map<String, int[]>> combiner = (map1, map2) -> {
            map2.entrySet().forEach(entry -> putOrCombine(map1, entry.getKey(), entry.getValue()[0]));
            return map1;
        };

        private final Function<Map<String, int[]>, Stream<PersonPositionDuration>> finisher;

        /**
         * Combines JobHistoryEntry objects by position applying custom binary operator to durations.
         * Transforms result into a stream of {@link PersonPositionDuration} objects.
         * Two convenience shortcuts are available: {@code getSummingInstance} and {@code getMaxingInstance}.
         *
         * @param forPerson        person to put into resulting objects
         * @param durationCombiner Operator to apply to durations when combining
         * @return Collector instance
         */
        public static JobHistoryCombiner getInstance(Person forPerson, IntBinaryOperator durationCombiner) {
            return new JobHistoryCombiner(forPerson, durationCombiner);
        }

        /**
         * Combines JobHistoryEntry objects by position adding up durations.
         * Transforms result into a stream of {@link PersonPositionDuration} objects.
         *
         * @param forPerson person to put into resulting objects
         * @return Collector instance
         */
        public static JobHistoryCombiner getSummingInstance(Person forPerson) {
            return new JobHistoryCombiner(forPerson, SUMMING_COMBINER);
        }

        /**
         * Combines JobHistoryEntry objects by position selecting longest durations.
         * Transforms result into a stream of {@link PersonPositionDuration} objects.
         *
         * @param forPerson person to put into resulting objects
         * @return Collector instance
         */
        public static JobHistoryCombiner getMaxingInstance(Person forPerson) {
            return new JobHistoryCombiner(forPerson, Math::max);
        }

        @Override
        public Supplier<Map<String, int[]>> supplier() {
            return HashMap::new;
        }

        @Override
        public BiConsumer<Map<String, int[]>, JobHistoryEntry> accumulator() {
            return accumulator;
        }

        @Override
        public BinaryOperator<Map<String, int[]>> combiner() {
            return combiner;
        }

        @Override
        public Function<Map<String, int[]>, Stream<PersonPositionDuration>> finisher() {
            return finisher;
        }

        @Override
        public Set<Characteristics> characteristics() {
            return Collections.unmodifiableSet(EnumSet.of(
                    Characteristics.UNORDERED));
        }
    }

    private Map<String, Person> getCoolestByPositionWithCustomCollector(List<Employee> employees) {
        return employees.stream()
                .flatMap(e -> e.getJobHistory().stream()
                        .collect(JobHistoryCombiner.getMaxingInstance(e.getPerson())))
                .collect(CollectCoolest.getInstance());
    }

    private Map<String, Person> getCoolestByPosition2WithCustomCollector(List<Employee> employees) {
        return employees.stream()
                .flatMap(e -> e.getJobHistory().stream()
                        .collect(JobHistoryCombiner.getSummingInstance(e.getPerson())))
                .collect(CollectCoolest.getInstance());
    }

    private List<Employee> getEmployees() {
        return Arrays.asList(
                new Employee(
                        new Person("John", "Galt", 20),
                        Arrays.asList(
                                new JobHistoryEntry(3, "dev", "epam"),
                                new JobHistoryEntry(2, "dev", "google")
                        )),
                new Employee(
                        new Person("John", "Doe", 21),
                        Arrays.asList(
                                new JobHistoryEntry(4, "BA", "yandex"),
                                new JobHistoryEntry(2, "QA", "epam"),
                                new JobHistoryEntry(2, "dev", "abc")
                        )),
                new Employee(
                        new Person("John", "White", 22),
                        Collections.singletonList(
                                new JobHistoryEntry(6, "QA", "epam")
                        )),
                new Employee(
                        new Person("John", "Galt", 23),
                        Arrays.asList(
                                new JobHistoryEntry(3, "dev", "epam"),
                                new JobHistoryEntry(3, "dev", "google")
                        )),
                new Employee(
                        new Person("John", "Doe", 24),
                        Arrays.asList(
                                new JobHistoryEntry(4, "QA", "yandex"),
                                new JobHistoryEntry(2, "BA", "epam"),
                                new JobHistoryEntry(2, "dev", "abc")
                        )),
                new Employee(
                        new Person("John", "White", 25),
                        Collections.singletonList(
                                new JobHistoryEntry(6, "QA", "epam")
                        )),
                new Employee(
                        new Person("John", "Galt", 26),
                        Arrays.asList(
                                new JobHistoryEntry(3, "dev", "epam"),
                                new JobHistoryEntry(1, "dev", "google")
                        )),
                new Employee(
                        new Person("Bob", "Doe", 27),
                        Arrays.asList(
                                new JobHistoryEntry(4, "QA", "yandex"),
                                new JobHistoryEntry(2, "QA", "epam"),
                                new JobHistoryEntry(2, "dev", "abc")
                        )),
                new Employee(
                        new Person("John", "White", 28),
                        Collections.singletonList(
                                new JobHistoryEntry(6, "BA", "epam")
                        )),
                new Employee(
                        new Person("John", "Galt", 29),
                        Arrays.asList(
                                new JobHistoryEntry(3, "dev", "epam"),
                                new JobHistoryEntry(1, "dev", "google")
                        )),
                new Employee(
                        new Person("John", "Doe", 30),
                        Arrays.asList(
                                new JobHistoryEntry(4, "QA", "yandex"),
                                new JobHistoryEntry(2, "QA", "epam"),
                                new JobHistoryEntry(5, "dev", "abc")
                        )),
                new Employee(
                        new Person("Bob", "White", 31),
                        Collections.singletonList(
                                new JobHistoryEntry(6, "QA", "epam")
                        ))
        );
    }

}
