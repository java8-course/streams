package part2.exercise;

import data.Employee;
import data.JobHistoryEntry;
import data.Person;
import org.junit.Test;

import java.util.*;
import java.util.function.Function;
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

        System.out.println("___________");

        final Map<String, Person> coolestByPositionAlt = getCoolestByPositionAlternative(getEmployees());
        coolestByPositionAlt.forEach((position, person) -> System.out.println(position + " -> " + person));
        assertEquals(coolestByPosition, coolestByPositionAlt);
        assertEquals(coolestByPosition.get("QA"), new Person("John", "White", 22));

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
        final Stream<PersonPositionDuration> personPositionDurationStream = employees.stream()
                .flatMap(
                        e -> e.getJobHistory()
                                .stream()
                                .map(j -> new PersonPositionDuration(e.getPerson(), j.getPosition(), j.getDuration())));
        return  personPositionDurationStream
                .collect(groupingBy(
                        PersonPositionDuration::getPosition,
                        collectingAndThen(
                                maxBy(comparing(PersonPositionDuration::getDuration)), p -> p.get().getPerson())));
    }

    private Map<String, Person> getCoolestByPositionAlternative(List<Employee> employees) {
        // Second option
        // Collectors.toMap
        // iterate twice: stream...collect(...).stream()...
        final Stream<PersonPositionDuration> personPositionDurationStream = employees.stream()
                .flatMap(
                        e -> e.getJobHistory()
                                .stream()
                                .map(j -> new PersonPositionDuration(e.getPerson(), j.getPosition(), j.getDuration())));

        return personPositionDurationStream
                .collect(Collectors.toMap(PersonPositionDuration::getPosition,
                        Function.identity(),
                        (p1, p2) -> p1.getDuration() >= p2.getDuration() ? p1 : p2))
                .entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        k -> k.getValue().getPerson()
                ));
    }



    @Test
    public void getTheCoolestOne2() {
        final Map<String, Person> coolestByPosition = getCoolestByPosition2(getEmployees());

        coolestByPosition.forEach((position, person) -> System.out.println(position + " -> " + person));
        assertEquals(coolestByPosition.get("BA"),new Person("John", "White", 28) );
    }

    private Stream<PersonPositionDuration> getDurationPerPosistion(Employee employee) {
        final Map<String, Integer> collect = employee.getJobHistory().stream()
                .collect(groupingBy(JobHistoryEntry::getPosition,
                        summingInt(JobHistoryEntry::getDuration)));

        return collect.entrySet().stream().map(t -> new PersonPositionDuration(employee.getPerson(), t.getKey(), t.getValue()));
    }


    // With the longest sum duration on this position
    // { John Doe, [{dev, google, 4}, {dev, epam, 4}] } предпочтительнее, чем { A B, [{dev, google, 6}, {QA, epam, 100}]}
    private Map<String, Person> getCoolestByPosition2(List<Employee> employees) {
        return employees.stream()
                .map(this::getDurationPerPosistion)
                .flatMap(Function.identity())
                .collect(groupingBy(PersonPositionDuration::getPosition, collectingAndThen(
                        maxBy(Comparator.comparing(PersonPositionDuration::getDuration)),
                        p -> p.get().getPerson()
                )));
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
