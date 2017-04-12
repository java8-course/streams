package part1.exercise;

import data.Employee;
import data.Generator;
import data.JobHistoryEntry;
import data.Person;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.junit.Test;
import part3.exercise.stream.StreamsExercise;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static data.Generator.generateEmployeeList;
import static java.util.stream.Collectors.*;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

public class StreamsExercise2 {
    // https://youtu.be/kxgo7Y4cdA8 Сергей Куксенко и Алексей Шипилёв — Через тернии к лямбдам, часть 1
    // https://youtu.be/JRBWBJ6S4aU Сергей Куксенко и Алексей Шипилёв — Через тернии к лямбдам, часть 2

    // https://youtu.be/O8oN4KSZEXE Сергей Куксенко — Stream API, часть 1
    // https://youtu.be/i0Jr2l3jrDA Сергей Куксенко — Stream API, часть 2

    // TODO class PersonEmployerPair

    @Test
    public void employersStuffLists() {
        Map<String, List<Person>> employersStuffLists = null;// TODO
        List<Employee> employees = getEmployees();

        Stream<EmployerPersonPair> employerPersonPairStream = employees.stream()
                .flatMap(StreamsExercise2::toEmployerPersonPairs);

        employersStuffLists = employerPersonPairStream
                .collect(Collectors.groupingBy(
                        EmployerPersonPair::getEmployer,
                        mapping(EmployerPersonPair::getPerson, toList())));

        //expected
        Set<String> employers = employees.stream()
                .flatMap(e -> e.getJobHistory().stream()
                        .map(JobHistoryEntry::getEmployer))
                .collect(toSet());

        Map<String, List<Person>> expected = new HashMap<>();
        employers.forEach(employer -> {
            List<Person> persons = employees.stream()
                    .filter(e -> e.getJobHistory().stream()
                            .anyMatch(j -> j.getEmployer().equals(employer)))
                    .map(Employee::getPerson)
                    .collect(toList());
            expected.put(employer, persons);
        });

        assertThat(employersStuffLists, is(expected));
    }


    private static Stream<EmployerPersonPair> toEmployerPersonPairs(Employee employee) {
        return employee.getJobHistory().stream()
                .map(e -> e.getEmployer())
                .map(e -> new EmployerPersonPair(e, employee.getPerson()));
    }

    @AllArgsConstructor
    @Getter
    static class EmployerPersonPair {
        String employer;
        Person person;
    }

    @Test
    public void indexByFirstEmployer() {
        Map<String, List<Person>> employeesIndex = null;// TODO
        throw new UnsupportedOperationException();
    }

    @Test
    public void greatestExperiencePerEmployer() {
        Map<String, Person> employeesIndex = null;// TODO

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
