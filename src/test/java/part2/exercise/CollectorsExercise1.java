package part2.exercise;

import data.Employee;
import data.JobHistoryEntry;
import data.Person;
import org.junit.Test;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;
import java.util.stream.Stream;

import static java.util.Comparator.comparing;
import static java.util.function.Function.identity;
import static java.util.stream.Collectors.*;
import static org.hamcrest.Matchers.everyItem;
import static org.hamcrest.Matchers.isIn;
import static org.junit.Assert.assertThat;


public class CollectorsExercise1 {

    @Test
    public void getTheCoolestOne() {
        final Map<String, Person> expected = new HashMap<>();
        expected.put("QA", new Person("John", "White", 22));
        expected.put("dev", new Person("John", "Doe", 30));
        expected.put("BA", new Person("John", "White", 28));

        final Map<String, Person> coolestByPositionSingleIteration = getCoolestByPositionSingleIteration(getEmployees());
        assertThat(coolestByPositionSingleIteration.entrySet(), everyItem(isIn(expected.entrySet())));

        final Map<String, Person> coolestByPositionDoubleIteration = getCoolestByPositionDoubleIteration(getEmployees());
        assertThat(coolestByPositionDoubleIteration.entrySet(), everyItem(isIn(expected.entrySet())));

        final Map<String, Person> coolestByPositionCustomCollector = getCoolestByPositionCustomCollector(getEmployees());
        assertThat(coolestByPositionCustomCollector.entrySet(), everyItem(isIn(expected.entrySet())));

        expected.forEach((position, person) -> System.out.println(position + " -> " + person));
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

    private static Stream<PersonPositionDuration> personPositionDurationStream(List<Employee> employees) {
        return employees.stream()
                .flatMap(e -> e.getJobHistory().stream()
                        .map(j -> new PersonPositionDuration(e.getPerson(), j.getPosition(), j.getDuration())));
    }

    // With the longest duration on single job (single iteration)
    private Map<String, Person> getCoolestByPositionSingleIteration(List<Employee> employees) {
        return personPositionDurationStream(employees)
                .collect(groupingBy(
                        PersonPositionDuration::getPosition,
                        collectingAndThen(
                                maxBy(comparing(PersonPositionDuration::getDuration)), p -> p.get().getPerson())));
    }

    // With the longest duration on single job (double iteration)
    private Map<String, Person> getCoolestByPositionDoubleIteration(List<Employee> employees) {
        return personPositionDurationStream(employees)
                .collect(
                        toMap(PersonPositionDuration::getPosition, identity(), BinaryOperator.maxBy(comparing(PersonPositionDuration::getDuration))))
                .entrySet().stream()
                .collect(toMap(Map.Entry::getKey, e -> e.getValue().getPerson()));
    }

    private class CoolestByPositionCollector implements Collector<PersonPositionDuration, Map<String, PersonPositionDuration>, Map<String, Person>> {

        final BinaryOperator<PersonPositionDuration> remappingFunction = BinaryOperator.maxBy(Comparator.comparing(PersonPositionDuration::getDuration));

        @Override
        public Supplier<Map<String, PersonPositionDuration>> supplier() {
            return HashMap::new;
        }

        @Override
        public BiConsumer<Map<String, PersonPositionDuration>, PersonPositionDuration> accumulator() {
            return (m, p) -> {
                m.merge(p.getPosition(), p, remappingFunction);
            };
        }

        @Override
        public BinaryOperator<Map<String, PersonPositionDuration>> combiner() {
            return (a, b) -> {
                b.entrySet().forEach(e -> a.merge(e.getKey(), e.getValue(), remappingFunction));
                return a;
            };
        }

        @Override
        public Function<Map<String, PersonPositionDuration>, Map<String, Person>> finisher() {
            return (m) -> m.entrySet().stream()
                    .collect(toMap(Map.Entry::getKey, e -> e.getValue().getPerson()));
        }

        @Override
        public Set<Characteristics> characteristics() {
            return Collections.emptySet();
        }

    }

    private Map<String, Person> getCoolestByPositionCustomCollector(List<Employee> employees) {
        return personPositionDurationStream(employees)
                .collect(new CoolestByPositionCollector());
    }

    private static Stream<PersonPositionDuration> personPositionMaxDurationStream(List<Employee> employees) {
        return employees.stream()
                .flatMap(e -> e.getJobHistory().stream()
                        .collect(groupingBy(
                                JobHistoryEntry::getPosition, summingInt(JobHistoryEntry::getDuration)))
                        .entrySet().stream()
                        .map(es -> new PersonPositionDuration(e.getPerson(), es.getKey(), es.getValue())));
    }

    @Test
    public void getTheMostCool() {
        final Map<String, Person> expected = new HashMap<>();
        expected.put("QA", new Person("John", "White", 22));
        expected.put("dev", new Person("John", "Galt", 20));
        expected.put("BA", new Person("John", "White", 28));

        final Map<String, Person> coolestByPosition = getTheMostCoolByPosition(getEmployees());
        assertThat(coolestByPosition.entrySet(), everyItem(isIn(expected.entrySet())));

        coolestByPosition.forEach((position, person) -> System.out.println(position + " -> " + person));
    }

    // With the longest sum duration on this position
    // { John Doe, [{dev, google, 4}, {dev, epam, 4}] } предпочтительнее, чем { A B, [{dev, google, 6}, {QA, epam, 100}]}
    private Map<String, Person> getTheMostCoolByPosition(List<Employee> employees) {
        return personPositionMaxDurationStream(employees)
                .collect(groupingBy(
                        PersonPositionDuration::getPosition,
                        collectingAndThen(
                                maxBy(comparing(PersonPositionDuration::getDuration)), p -> p.get().getPerson())));
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
