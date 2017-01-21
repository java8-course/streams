package part2.exercise;

import data.Employee;
import data.JobHistoryEntry;
import data.Person;
import lombok.*;
import org.junit.Test;

import java.util.*;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.stream.Stream;


import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.*;
import static java.util.stream.Collectors.groupingBy;
import static org.junit.Assert.assertEquals;

public class CollectorsExercise1 {

    @Test
    public void getTheCoolestOne() {

        final Map<String, Person> coolestByPosition = getCoolestByPosition(getEmployees());

        assertEquals(coolestByPosition.get("QA"),new Person("Bob", "White", 31) );

    }

    @Getter
    @AllArgsConstructor
    private static class PersonPositionDuration {
        private final Person person;
        private final String position;
        private final int duration;
    }

    // With the longest duration on single job
    private Map<String, Person> getCoolestByPosition(final List<Employee> employees) {
        // First option
        // Collectors.maxBy
        // Collectors.collectingAndThen
        // Collectors.groupingBy

//        return employees.stream()
//                .flatMap(e -> e.getJobHistory().stream()
//                        .map(j-> new PersonPositionDuration(e.getPerson(),j.getPosition(),j.getDuration()))
//                )
//                .collect(groupingBy(PersonPositionDuration::getPosition, collectingAndThen(
//                        maxBy(comparing(PersonPositionDuration::getDuration)), p -> p.get().getPerson())));

        // Second option
        // Collectors.toMap
        // iterate twice: stream...collect(...).stream()...

        return employees.stream()
                .flatMap(e -> e.getJobHistory().stream()
                        .map(j-> new PersonPositionDuration(e.getPerson(),j.getPosition(),j.getDuration()))
                )
                .collect(toMap(PersonPositionDuration::getPosition, Function.identity(),
                        BinaryOperator.maxBy(Comparator.comparingInt(PersonPositionDuration::getDuration))))
                .entrySet().stream()
                .collect(toMap(Map.Entry::getKey, e -> e.getValue().getPerson()));

    }

    @Test
    public void getTheCoolestOne2() {
        final Map<String, Person> coolestByPosition = getCoolestByPosition2(getEmployees());
        coolestByPosition.forEach((position, person) -> System.out.println(position + " -> " + person));
}


    // With the longest sum duration on this position
    // { John Doe, [{dev, google, 4}
    // , {dev, epam, 4}] } предпочтительнее, чем { A B, [{dev, google, 6}, {QA, epam, 100}]}
    private Map<String, Person> getCoolestByPosition2(List<Employee> employees) {
        return employees.stream()
                .flatMap(flatMapping())
                .collect(groupingBy(PersonPositionDuration::getPosition, collectingAndThen(
                        maxBy(comparing(PersonPositionDuration::getDuration)), p -> p.get().getPerson()))
                );
    }

    private static Function<Employee, Stream<PersonPositionDuration>> flatMapping(){
        return e -> e.getJobHistory().stream()
                .collect(groupingBy(JobHistoryEntry::getPosition, summingInt(JobHistoryEntry::getDuration)))
                .entrySet().stream()
                        .map(j-> new PersonPositionDuration(e.getPerson(),j.getKey(),j.getValue()));
    }

    private List<Employee> getEmployees() {
        return Collections.unmodifiableList(Arrays.asList(
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
                                new JobHistoryEntry(666, "QA", "epam")
                        ))
        ));
    }

}
