package part1.exercise;

import data.Employee;
import data.JobHistoryEntry;
import data.Person;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.junit.Test;

import java.util.*;
import java.util.stream.Collectors;

import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class StreamsExercise2 {
    // https://youtu.be/kxgo7Y4cdA8 Сергей Куксенко и Алексей Шипилёв — Через тернии к лямбдам, часть 1
    // https://youtu.be/JRBWBJ6S4aU Сергей Куксенко и Алексей Шипилёв — Через тернии к лямбдам, часть 2

    // https://youtu.be/i0Jr2l3jrDA Сергей Куксенко — Stream API, часть 2
    // https://youtu.be/O8oN4KSZEXE Сергей Куксенко — Stream API, часть 1


    @Data
    @AllArgsConstructor
    class PersonEmployer{
        private Person person;
        private String employer;
    }

    @Data
    @AllArgsConstructor
    class PersonEmployerDuration{
        private final Person person;
        private final String employer;
        private final int duration;

    }

    @Test
    public void employersStuffLists() {

        Map<String, List<Person>> employersStuffLists = getEmployees().stream()
                .flatMap(
                        e -> e.getJobHistory()
                                .stream()
                                .map(j -> new PersonEmployer(e.getPerson(),j.getEmployer()))
                )
                .collect(groupingBy(PersonEmployer::getEmployer, mapping(PersonEmployer::getPerson, Collectors.toList())));

        assertTrue(employersStuffLists.get("epam").contains(new Person("John", "Galt", 20)));
    }

    @Test
    public void indexByFirstEmployer() {
        Map<String, List<Person>> employeesIndex = getEmployees().stream()
                .flatMap(
                        e -> e.getJobHistory()
                                .stream()
                                .limit(1)
                                .map(j -> new PersonEmployer(e.getPerson(), j.getEmployer()))
                )
                .collect(groupingBy(PersonEmployer::getEmployer, mapping(PersonEmployer::getPerson, Collectors.toList())));

        assertTrue(employeesIndex.get("epam").contains(new Person("John", "Galt", 20)));
        assertTrue(!employeesIndex.get("epam").contains(new Person("John", "Doe", 21)));

    }

    @Test
    public void greatestExperiencePerEmployer() {
        Map<String, Person> employeesIndex = getEmployees().stream()
                .flatMap(
                        e -> e.getJobHistory().stream()
                                .map(j -> new PersonEmployerDuration(e.getPerson(),j.getEmployer(), j.getDuration())

                                )
                )
                .collect(groupingBy(
                        PersonEmployerDuration::getEmployer, collectingAndThen(
                                maxBy(comparing(PersonEmployerDuration::getDuration)), p -> p.get().getPerson()))
                );

//        employeesIndex.entrySet().stream().forEach(System.out::println);

        assertEquals(new Person("John", "White", 28), employeesIndex.get("epam"));
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
                                new JobHistoryEntry(666, "BA", "epam")
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
