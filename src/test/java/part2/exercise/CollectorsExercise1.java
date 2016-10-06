package part2.exercise;

import data.Employee;
import data.JobHistoryEntry;
import data.Person;
import org.junit.Assert;
import org.junit.Test;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.*;
import static org.hamcrest.core.Is.is;

public class CollectorsExercise1 {

    @Test
    public void getTheCoolestOne() {
        final Map<String, Person> coolestByPosition = getCoolestByPosition(getEmployees());
        Assert.assertThat(coolestByPosition.get("dev"), is(new Person("John", "Galt", 23)));
    }

    @Test
    public void getTheCoolestOne2() {
        final Map<String, Person> coolestByPosition = getCoolestByPosition2(getEmployees());
        Assert.assertThat(coolestByPosition.get("dev"), is(new Person("John", "Galt", 23)));

    }

    @Test
    public void getTheCoolestOne3() {
        final Map<String, Person> coolestByPosition = getCoolestByPosition3(getEmployees());
        Assert.assertThat(coolestByPosition.get("dev"), is(new Person("John", "Galt", 23)));
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
        // First option
        // Collectors.maxBy
        // Collectors.collectingAndThen
        // Collectors.groupingBy
        return employees.stream()
                .flatMap(employee -> employee.getJobHistory().stream()
                        .collect(groupingBy(
                                JobHistoryEntry::getPosition,
                                summingInt(JobHistoryEntry::getDuration)))
                        .entrySet().stream()
                        .map(esd -> new PersonPositionDuration(employee.getPerson(), esd.getKey(), esd.getValue())))
                .collect(groupingBy(
                        PersonPositionDuration::getPosition,
                        collectingAndThen(
                                maxBy(Comparator.comparing(PersonPositionDuration::getDuration)), o -> o.get().getPerson())));
    }

    // TODO
    // With the longest sum duration on this position
    // { John Doe, [{dev, google, 4}, {dev, epam, 4}] } предпочтительнее, чем { A B, [{dev, google, 6}, {QA, epam, 100}]}
    private Map<String, Person> getCoolestByPosition2(List<Employee> employees) {
        // Second option
        // Collectors.toMap
        // iterate twice: stream...collect(...).stream()...
        return employees.stream()
                .flatMap(employee -> employee.getJobHistory().stream()
                        .collect(toMap(JobHistoryEntry::getPosition,
                                JobHistoryEntry::getDuration,
                                (integer, integer2) -> integer + integer2))
                        .entrySet().stream()
                        .map(pde -> new PersonPositionDuration(employee.getPerson(), pde.getKey(), pde.getValue())))
                .collect(toMap(PersonPositionDuration::getPosition,
                        Function.identity(),
                        BinaryOperator.maxBy(Comparator.comparing(PersonPositionDuration::getDuration))))
                .entrySet().stream()
                .collect(toMap(Map.Entry::getKey, entry -> entry.getValue().getPerson()));
    }

    private Map<String, Person> getCoolestByPosition3(List<Employee> employees) {
        // написать свой коллектор
        Collector<Employee, Map<String, PersonPositionDuration>, Map<String, Person>> EmployeeToMapCollector = new Collector<Employee, Map<String, PersonPositionDuration>, Map<String, Person>>() {
            @Override
            public Supplier<Map<String, PersonPositionDuration>> supplier() {
                return HashMap::new;
            }

            @Override
            public BiConsumer<Map<String, PersonPositionDuration>, Employee> accumulator() {
                return (accum, element) -> element.getJobHistory().stream()
                        .collect(toMap (JobHistoryEntry::getPosition,
                                        JobHistoryEntry::getDuration,
                                        (integer, integer2) -> integer + integer2))
                        .entrySet().stream()
                        .filter(positionDurationEntry -> !accum.containsKey(positionDurationEntry.getKey())
                                || accum.get(positionDurationEntry.getKey()).getDuration() < positionDurationEntry.getValue())
                        .map(pde -> new PersonPositionDuration(element.getPerson(), pde.getKey(), pde.getValue()))
                        .forEach(ppd -> accum.put(ppd.getPosition(), ppd));
            }

            @Override
            public BinaryOperator<Map<String, PersonPositionDuration>> combiner() {
                return (accum1, accum2) -> {
                    accum2.values().stream()
                            .filter(ppd -> !accum1.containsKey(ppd.getPosition())
                                    || accum1.get(ppd.getPosition()).getDuration() < ppd.getDuration())
                            .forEach(ppd -> accum1.put(ppd.getPosition(), ppd));
                    return accum1;
                };
            }

            @Override
            public Function<Map<String, PersonPositionDuration>, Map<String, Person>> finisher() {
                return accum -> accum.entrySet().stream()
                        .collect(toMap(Map.Entry::getKey, entry -> entry.getValue().getPerson()));
            }

            @Override
            public Set<Characteristics> characteristics() {
                return Collections.unmodifiableSet(EnumSet.of(
                        Characteristics.UNORDERED));
            }
        };

        return employees.stream()
                .collect(EmployeeToMapCollector);

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
                                new JobHistoryEntry(4, "dev", "google")
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
