package part2.exercise;

import data.Employee;
import data.JobHistoryEntry;
import data.Person;
import org.junit.Test;

import java.util.*;
import java.util.function.*;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.*;
import static org.junit.Assert.assertEquals;

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

    // With the longest duration on single job
    private Map<String, Person> getCoolestByPosition(List<Employee> employees) {
        final Stream<PersonPositionDuration> personPositionDurationStream = employees.stream()
                .flatMap(
                        e -> e.getJobHistory()
                                .stream()
                                .map(j -> new PersonPositionDuration(e.getPerson(), j.getPosition(), j.getDuration())));

        // First option
        Map<String, Person> collect1 = personPositionDurationStream
                .collect(Collectors.groupingBy(PersonPositionDuration::getPosition,
                        collectingAndThen(
                                maxBy(comparing(PersonPositionDuration::getDuration)), p -> p.get().getPerson())));

        // Second option
        Map<String, PersonPositionDuration> map = personPositionDurationStream
                .collect(Collectors.toMap(PersonPositionDuration::getPosition,
                        Function.identity(),
                        BinaryOperator.maxBy(comparing(PersonPositionDuration::getDuration))));

        Map<String, Person> collect2 = map.entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey,
                        e -> e.getValue().getPerson()));

        // Third option
        Map<String, Person> collect3 = personPositionDurationStream
                .collect(new Collector<PersonPositionDuration, Map<String, PersonPositionDuration>, Map<String, Person>>() {
                    private BiFunction biFunction = BinaryOperator.maxBy(comparing(PersonPositionDuration::getDuration));

                    @Override
                    public Supplier<Map<String, PersonPositionDuration>> supplier() {
                        return HashMap::new;
                    }

                    @Override
                    public BiConsumer<Map<String, PersonPositionDuration>, PersonPositionDuration> accumulator() {
                        return (map, ppd) -> map.merge(ppd.getPosition(), ppd, biFunction);
                    }

                    @Override
                    public BinaryOperator<Map<String, PersonPositionDuration>> combiner() {
                        return (map1, map2) -> {
                            map2.forEach((key, value) -> map1.merge(key, value, biFunction));
                            return map1;
                        };
                    }

                    @Override
                    public Function<Map<String, PersonPositionDuration>, Map<String, Person>> finisher() {
                        return (map) -> map.entrySet().stream()
                                .collect(Collectors.toMap(Map.Entry::getKey, entry -> entry.getValue().getPerson()));
                    }

                    @Override
                    public Set<Characteristics> characteristics() {
                        return Collections.emptySet();
                    }
                });
        return collect1;
    }

    @Test
    public void getTheCoolestOne2() {
        final Map<String, Person> coolestByPosition = getCoolestByPosition2(getEmployees());

        assertEquals(new Person("John", "Doe", 30), coolestByPosition.get("QA"));
        assertEquals(new Person("John", "Galt", 26), coolestByPosition.get("dev"));
        assertEquals(new Person("John", "Doe", 24), coolestByPosition.get("BA"));
    }

    // With the longest sum duration on this position
    // { John Doe, [{dev, google, 4}, {dev, epam, 4}] } предпочтительнее, чем { A B, [{dev, google, 6}, {QA, epam, 100}]}
    private Map<String, Person> getCoolestByPosition2(List<Employee> employees) {
        return employees.stream()
                .flatMap(this::employeeToPersonPositionDurationStream)
                .collect(groupingBy(PersonPositionDuration::getPosition,
                        collectingAndThen(
                                maxBy(Comparator.comparing(PersonPositionDuration::getDuration)),
                                personEmployerDuration -> personEmployerDuration.get().getPerson())
                        )
                );
    }

    private Stream<PersonPositionDuration> employeeToPersonPositionDurationStream(Employee employee) {
        Map<String, Integer> map = employee.getJobHistory().stream()
                .collect(groupingBy(JobHistoryEntry::getPosition,
                        Collectors.summingInt(JobHistoryEntry::getDuration)));

        return map.entrySet().stream()
                .map(e -> new PersonPositionDuration(employee.getPerson(), e.getKey(), e.getValue()));
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
                                new JobHistoryEntry(4, "BA", "epam"),
                                new JobHistoryEntry(2, "dev", "abc"),
                                new JobHistoryEntry(4, "BA", "google")
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
                                new JobHistoryEntry(4, "dev", "google")
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
                                new JobHistoryEntry(4, "QA", "epam"),
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
