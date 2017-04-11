package part2.exercise;

import data.Employee;
import data.JobHistoryEntry;
import data.Person;
import org.junit.Test;

import java.util.*;
import java.util.function.*;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.*;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;

public class CollectorsExercise1 {

    @Test
    public void getTheCoolestOne() {
        final Map<String, Person> coolestByPositionVersion1 = getCoolestByPositionVersion1(getEmployees());
        final Map<String, Person> coolestByPositionVersion2 = getCoolestByPositionVersion2(getEmployees());
        final Map<String, Person> coolestByPositionVersion3 = getCoolestByPositionVersion3(getEmployees());

        assertThat(coolestByPositionVersion1.get("BA"), equalTo(coolestByPositionVersion2.get("BA")));
        assertThat(coolestByPositionVersion1.get("BA"), equalTo(coolestByPositionVersion3.get("BA")));
        assertThat(coolestByPositionVersion1.get("dev"), equalTo(coolestByPositionVersion2.get("dev")));
        assertThat(coolestByPositionVersion1.get("dev"), equalTo(coolestByPositionVersion3.get("dev")));
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
    private Map<String, Person> getCoolestByPositionVersion1(List<Employee> employees) {
        // First option
        // Collectors.maxBy
        // Collectors.collectingAndThen
        // Collectors.groupingBy

        // Second option
        // Collectors.toMap
        // iterate twice: stream...collect(...).stream()...
        return employees.stream()
                .flatMap(e -> e.getJobHistory()
                        .stream()
                        .map(j -> new PersonPositionDuration(
                                e.getPerson(),
                                j.getPosition(),
                                j.getDuration()))
                )
                .collect(groupingBy(
                        PersonPositionDuration::getPosition,
                        collectingAndThen(maxBy(
                                comparing(PersonPositionDuration::getDuration)),
                                p -> p.get().getPerson())));
    }

    private Map<String, Person> getCoolestByPositionVersion2(List<Employee> employees) {
        return employees.stream()
                .flatMap(e -> e.getJobHistory()
                        .stream()
                        .map(j -> new PersonPositionDuration(
                                e.getPerson(),
                                j.getPosition(),
                                j.getDuration())))
                .collect(Collectors.toMap(
                        PersonPositionDuration::getPosition,
                        Function.identity(),
                        (d1, d2) -> d1.getDuration() > d2.getDuration() ? d1 : d2))
                .entrySet().stream()
                .collect(toMap(
                        Map.Entry::getKey,
                        e -> e.getValue().getPerson()));
    }

    private Map<String, Person> getCoolestByPositionVersion3(List<Employee> employees) {
        return employees.stream()
                .flatMap(e -> e.getJobHistory()
                        .stream()
                        .map(j -> new PersonPositionDuration(
                                e.getPerson(),
                                j.getPosition(),
                                j.getDuration())))
                .collect(new Collector<PersonPositionDuration, Map<String, PersonPositionDuration>, Map<String, Person>>() {

                    @Override
                    public Supplier<Map<String, PersonPositionDuration>> supplier() {
                        return HashMap::new;
                    }

                    @Override
                    public BiConsumer<Map<String, PersonPositionDuration>, PersonPositionDuration> accumulator() {
                        return (map, p) -> map.merge(
                                p.getPosition(),
                                p,
                                (p1, p2) -> p1.getDuration() > p2.getDuration() ? p1 : p2);
                    }

                    @Override
                    public BinaryOperator<Map<String, PersonPositionDuration>> combiner() {
                        return (map1, map2) -> {
                            map1.putAll(
                                    map2.entrySet().stream()
                                            .filter(el -> !map1.containsKey(el.getKey())
                                                    || map1.get(el.getKey()).getDuration() < el.getValue().getDuration())
                                            .collect(toMap(
                                                    Map.Entry::getKey,
                                                    Map.Entry::getValue))
                            );
                            return map1;
                        };
                    }

                    @Override
                    public Function<Map<String, PersonPositionDuration>, Map<String, Person>> finisher() {
                        return map -> map.entrySet().stream()
                                .collect(toMap(
                                        Map.Entry::getKey,
                                        e -> e.getValue().getPerson()));
                    }

                    @Override
                    public Set<Characteristics> characteristics() {
                        return Collections.EMPTY_SET;
                    }
                });
    }


    @Test
    public void getTheCoolestOne2() {
        final Map<String, Person> coolestByPosition = getCoolestByPosition2(getEmployees());

        assertThat(coolestByPosition.size(), is(3));
        assertThat(coolestByPosition.get("BA"), equalTo(new Person("John", "White", 28)));
        assertThat(coolestByPosition.get("QA"), equalTo(new Person("John", "Doe", 24)));
    }

    private static Map<String, Integer> durationOnPositions(List<JobHistoryEntry> jobHistory) {
        return jobHistory.stream()
                .collect(groupingBy(
                        JobHistoryEntry::getPosition,
                        summingInt(JobHistoryEntry::getDuration)));
    }

    // With the longest sum duration on this position
    // { John Doe, [{dev, google, 4}, {dev, epam, 4}] } предпочтительнее, чем { A B, [{dev, google, 6}, {QA, epam, 100}]}
    private Map<String, Person> getCoolestByPosition2(List<Employee> employees) {
        return employees.stream()
                .flatMap(employee -> durationOnPositions(employee.getJobHistory())
                        .entrySet().stream()
                        .map(position -> new PersonPositionDuration(
                                employee.getPerson(),
                                position.getKey(),
                                position.getValue()))
                )
                .collect(groupingBy(
                        PersonPositionDuration::getPosition,
                        collectingAndThen(
                                maxBy(comparing(PersonPositionDuration::getDuration)),
                                p -> p.get().getPerson())));
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
                                new JobHistoryEntry(2, "QA", "epam"),
                                new JobHistoryEntry(2, "BA", "epam"),
                                new JobHistoryEntry(1, "QA", "abc"),
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
