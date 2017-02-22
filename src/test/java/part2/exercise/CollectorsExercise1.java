package part2.exercise;

import data.Employee;
import data.JobHistoryEntry;
import data.Person;
import org.junit.Test;

import java.util.*;
import java.util.function.BinaryOperator;
import java.util.function.Function;
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

    private static Stream<PersonPositionDuration> employeeInfo(Employee employee) {
        return employee.getJobHistory().stream().map(e -> new PersonPositionDuration(employee.getPerson(), e.getPosition(), e.getDuration()));
    }

    // With the longest duration on single job
    private Map<String, Person> getCoolestByPosition(List<Employee> employees) {
        // First option
        // Collectors.maxBy
        // Collectors.collectingAndThen
        // Collectors.groupingBy

        Map<String, Person> firstOptionMap = employees.stream().parallel().flatMap(e -> e.getJobHistory().stream().map(jobHistoryEntry ->
                new PersonPositionDuration(e.getPerson(), jobHistoryEntry.getPosition(), jobHistoryEntry.getDuration())))
                .collect(groupingBy(PersonPositionDuration::getPosition, collectingAndThen(maxBy(Comparator.comparing(PersonPositionDuration::getDuration)),
                        j -> j.get().getPerson())));


        // Second option
        // Collectors.toMap
        // iterate twice: stream...collect(...).stream()...
        // TODO
        Map<String, Person> secondOptionMap = employees.stream().parallel().flatMap(e -> e.getJobHistory().stream().map(jobHistoryEntry ->
                new PersonPositionDuration(e.getPerson(), jobHistoryEntry.getPosition(), jobHistoryEntry.getDuration())))
                .collect(toMap(PersonPositionDuration::getPosition, Function.identity(),
                        BinaryOperator.maxBy(Comparator.comparing(PersonPositionDuration::getDuration)))).entrySet()
                .stream().collect(toMap(Map.Entry::getKey, j -> j.getValue().getPerson()));
        return secondOptionMap;
    }

    @Test
    public void getTheCoolestOne2() {
        final Map<String, Person> coolestByPosition = getCoolestByPosition2(getEmployees());
        coolestByPosition.forEach((position, person) -> System.out.println(position + " -> " + person));
    }

    private static Map<String,Person> getCoolestPositionByStream(Stream<PersonPositionDuration> personPositionDurationStream){
        return personPositionDurationStream.collect(toMap(PersonPositionDuration::getPosition, Function.identity(),
                (p1,p2) -> p2.getDuration() > p1.getDuration() ? p2 : p1)).entrySet().stream()
                .collect(toMap(Map.Entry::getKey, e -> e.getValue().getPerson()));
    }

    private static Stream<PersonPositionDuration> employeeInfoDur(Employee employee) {
        return employeeInfo(employee).collect(groupingBy(PersonPositionDuration::getPosition)).entrySet().stream()
                .map(e -> new PersonPositionDuration(employee.getPerson(), e.getKey(),
                        e.getValue().stream().mapToInt(PersonPositionDuration::getDuration).sum()));
    }

    // With the longest sum duration on this position
    // { John Doe, [{dev, google, 4}, {dev, epam, 4}] } предпочтительнее, чем { A B, [{dev, google, 6}, {QA, epam, 100}]}
    private Map<String, Person> getCoolestByPosition2(List<Employee> employees) {
        return getCoolestPositionByStream(employees.stream().flatMap(CollectorsExercise1::employeeInfoDur));
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
