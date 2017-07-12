package part2.exercise;

import data.Employee;
import data.JobHistoryEntry;
import data.Person;
import org.junit.Test;

import java.util.*;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
        Map<String, Person> collect = employees.stream()
                .flatMap(
                        this::toPersonPositionDuration
                )
                .collect(
                        groupingBy(
                                PersonPositionDuration::getPosition,
                                collectingAndThen(maxBy(Comparator.comparing(PersonPositionDuration::getDuration)), p -> p.get().getPerson())
                        )
                );

        Map<String, Person> collect1 = employees.stream()
                .flatMap(this::toPersonPositionDuration)
                .collect(Collectors.toMap(
                        PersonPositionDuration::getPosition,
                        Function.identity(),
                        BinaryOperator.maxBy(Comparator.comparing(PersonPositionDuration::getDuration))
                ))
                .entrySet().stream()
                .collect(
                        toMap(
                                Map.Entry::getKey,
                                t -> t.getValue().getPerson()
                        )
                );


        assertEquals(collect, collect);

        return collect;
    }

    private Stream<? extends PersonPositionDuration> toPersonPositionDuration(Employee employee) {
        return employee.getJobHistory().stream()
                .map(jobHistoryEntry -> new PersonPositionDuration(employee.getPerson(), jobHistoryEntry.getPosition(), jobHistoryEntry.getDuration()));
    }

    @Test
    public void getTheCoolestOne2() {
        final Map<String, Person> coolestByPosition = getCoolestByPosition2(getEmployees());

        coolestByPosition.forEach((position, person) -> System.out.println(position + " -> " + person));
    }

    // With the longest sum duration on this position
    // { John Doe, [{dev, google, 4}, {dev, epam, 4}] } предпочтительнее, чем { A B, [{dev, google, 6}, {QA, epam, 100}]}
    private Map<String, Person> getCoolestByPosition2(List<Employee> employees) {
        return employees.stream()
                .flatMap(this::toExclusivePersonPositionDuration)
                .collect(
                        groupingBy(
                                PersonPositionDuration::getPosition,
                                collectingAndThen(maxBy(Comparator.comparing(PersonPositionDuration::getDuration)), p -> p.get().getPerson())
                        )
                );
                /*.collect(
                        toMap(
                                PersonPositionDuration::getPosition,
                                Function.identity(),
                                (p1, p2) -> p1.getDuration() > p2.getDuration() ? p1 : p2
                        )
                ).entrySet()
                .stream()
                .collect(
                        toMap(
                                Map.Entry::getKey,
                                e -> e.getValue().getPerson()
                        )
                );*/
    }

    private Stream<? extends PersonPositionDuration> toExclusivePersonPositionDuration(Employee employee) {
        return employee.getJobHistory()
                .stream()
                .collect(
                        toMap(JobHistoryEntry::getPosition,
                                JobHistoryEntry::getDuration,
                                (d1, d2) -> d1 + d2
                        )
                )
                .entrySet()
                .stream()
                .map(
                        entry -> new PersonPositionDuration(employee.getPerson(), entry.getKey(), entry.getValue())
                );
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
