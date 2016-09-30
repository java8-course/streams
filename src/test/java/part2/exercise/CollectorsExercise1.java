package part2.exercise;

import data.Employee;
import data.JobHistoryEntry;
import data.Person;
import org.junit.Test;

import java.util.*;
import java.util.function.*;
import java.util.stream.Collector;
import java.util.stream.Stream;

import static java.util.stream.Collectors.*;

public class CollectorsExercise1 {

    @Test
    public void getTheCoolestOne() {
        final Map<String, Person> coolestByPosition = getCoolestByPosition(getEmployees());

        coolestByPosition.forEach((position, person) -> System.out.println(position + " -> " + person));
    }

    private static class PersonPositionDuration {
        private final Person person;
        private final String position;
        private final int duration;

        public PersonPositionDuration(Person person, String position, int duration) {
            this.person = person;
            this.position = position;
            this.duration = duration;
        }

        public Person getPerson() {
            return person;
        }

        public String getPosition() {
            return position;
        }

        public int getDuration() {
            return duration;
        }
    }

    private static Stream<PersonPositionDuration> getPPDStream(List<Employee> employees) {
        return employees.stream()
                .flatMap(e -> e.getJobHistory().stream()
                        .map(
                                entry -> new PersonPositionDuration(e.getPerson(), entry.getPosition(), entry.getDuration())
                        )
                );
    }

    // With the longest duration on single job
    private Map<String, Person> getCoolestByPosition(List<Employee> employees) {

        Map<String, Person> map1 = getPPDStream(employees)
                .collect(
                        groupingBy(
                                PersonPositionDuration::getPosition,
                                collectingAndThen(
                                        maxBy(Comparator.comparing(PersonPositionDuration::getDuration)),
                                        p -> p.get().getPerson()
                                )
                        )
                );

        Map<String, Person> map2 = getPPDStream(employees)
                .collect(
                        toMap(
                                PersonPositionDuration::getPosition,
                                Function.identity(),
                                BinaryOperator.maxBy(Comparator.comparing(PersonPositionDuration::getDuration))
                        )
                )
                .entrySet().stream()
                .collect(
                        toMap(
                                Map.Entry::getKey,
                                entry -> entry.getValue().getPerson()
                        )
                );

        //using custom collector
        return getPPDStream(employees)
                .collect(new CoolestPositionCollector());
    }

    private static class CoolestPositionCollector implements Collector<PersonPositionDuration, Map<String, PersonPositionDuration>, Map<String, Person>> {

        private final BinaryOperator<PersonPositionDuration> combiner = BinaryOperator.maxBy(
                Comparator.comparing(PersonPositionDuration::getDuration));

        @Override
        public Supplier<Map<String, PersonPositionDuration>> supplier() {
            return HashMap::new;
        }

        @Override
        public BiConsumer<Map<String, PersonPositionDuration>, PersonPositionDuration> accumulator() {
            return (map, ppd) -> map.merge(ppd.getPosition(), ppd, combiner);
        }

        @Override
        public BinaryOperator<Map<String, PersonPositionDuration>> combiner() {
            return (m1, m2) -> {
                for (Map.Entry<String, PersonPositionDuration> e : m2.entrySet()) {
                    m1.merge(e.getKey(), e.getValue(), combiner);
                }
                return m1;
            };
        }

        @Override
        public Function<Map<String, PersonPositionDuration>, Map<String, Person>> finisher() {
            return map -> map.entrySet().stream()
                    .collect(
                            toMap(
                                    Map.Entry::getKey,
                                    entry -> entry.getValue().getPerson()
                            )
                    );
        }

        @Override
        public Set<Characteristics> characteristics() {
            return Collections.emptySet();
        }
    }

    @Test
    public void getTheCoolestOne2() {
        final Map<String, Person> coolestByPosition = getCoolestByPosition2(getEmployees());

        coolestByPosition.forEach((position, person) -> System.out.println(position + " -> " + person));
    }

    // With the longest sum duration on this position
    // { John Doe, [{dev, google, 4}, {dev, epam, 4}] } предпочтительнее, чем { A B, [{dev, google, 6}, {QA, epam, 100}]}
    private Map<String, Person> getCoolestByPosition2(List<Employee> employees) {

        Map<String, Person> map1 = employees.stream()
                .flatMap(e -> e.getJobHistory().stream()
                        .collect(
                                groupingBy(
                                        JobHistoryEntry::getPosition,
                                        summingInt(JobHistoryEntry::getDuration)
                                )
                        )
                        .entrySet().stream()
                        .map(entry -> new PersonPositionDuration(e.getPerson(), entry.getKey(), entry.getValue()))
                )
                .collect(
                        groupingBy(
                                PersonPositionDuration::getPosition,
                                collectingAndThen(
                                        maxBy(Comparator.comparing(PersonPositionDuration::getDuration)),
                                        p -> p.get().getPerson()
                                )
                        )
                );

        Map<String, Person> map2 = employees.stream()
                .flatMap(e -> e.getJobHistory().stream()
                        .collect(new JobHistoryCollector())
                        .entrySet().stream()
                        .map(entry -> new PersonPositionDuration(e.getPerson(), entry.getKey(), entry.getValue()))
                )
                .collect(new CoolestPositionCollector());

        return map1;
    }

    private static class JobHistoryCollector implements Collector<JobHistoryEntry, Map<String, Integer>, Map<String, Integer>> {

        private final BiFunction<Integer, Integer, Integer> combiner = Integer::sum;

        @Override
        public Supplier<Map<String, Integer>> supplier() {
            return HashMap::new;
        }

        @Override
        public BiConsumer<Map<String, Integer>, JobHistoryEntry> accumulator() {
            return (map, entry) -> map.merge(entry.getPosition(), entry.getDuration(), combiner);
        }

        @Override
        public BinaryOperator<Map<String, Integer>> combiner() {
            return (m1, m2) -> {
                for (Map.Entry<String, Integer> e : m2.entrySet()) {
                    m1.merge(e.getKey(), e.getValue(), combiner);
                }
                return m1;
            };
        }

        @Override
        public Function<Map<String, Integer>, Map<String, Integer>> finisher() {
            return Function.identity();
        }

        @Override
        public Set<Characteristics> characteristics() {
            return Collections.unmodifiableSet(EnumSet.of(Collector.Characteristics.IDENTITY_FINISH));
        }
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
                                new JobHistoryEntry(2, "dev", "google")
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
