package part1.exercise;

import data.Employee;
import data.JobHistoryEntry;
import data.Person;
import org.junit.Test;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static data.Generator.generateEmployeeList;
import static java.util.stream.Collectors.*;
import static org.junit.Assert.assertEquals;

public class StreamsExercise2 {
    // https://youtu.be/kxgo7Y4cdA8 Сергей Куксенко и Алексей Шипилёв — Через тернии к лямбдам, часть 1
    // https://youtu.be/JRBWBJ6S4aU Сергей Куксенко и Алексей Шипилёв — Через тернии к лямбдам, часть 2

    // https://youtu.be/O8oN4KSZEXE Сергей Куксенко — Stream API, часть 1
    // https://youtu.be/i0Jr2l3jrDA Сергей Куксенко — Stream API, часть 2

    // TODO class PersonEmployerPair

    class PersonEmployerPair {
        final Person person;

        public PersonEmployerPair(Person person, String employer) {
            this.person = person;
            this.employer = employer;
        }

        String employer;

        public Person getPerson() {
            return person;
        }

        public String getEmployer() {
            return employer;
        }
    }

    @Test
    public void employersStuffLists() {
        getEmployees().stream()
                .flatMap(e -> e.getJobHistory().stream()
                        .map(j -> new PersonEmployerPair(e.getPerson(), j.getEmployer())))
                .collect(collectingAndThen(groupingBy(e -> e.getEmployer()),
                        e -> e.entrySet().stream().collect(toMap(j -> j.getKey(), j -> j.getValue().stream()
                                .map(k -> k.getPerson()))))
                );
    }

    @Test
    public void indexByFirstEmployer() {
        Map<String, List<Person>> employeesIndex = getEmployees().stream()
                .map(e -> new PersonEmployerPair(e.getPerson(), e.getJobHistory().get(0).getEmployer()))
                .collect(Collectors.groupingBy(PersonEmployerPair::getEmployer, Collectors.mapping(PersonEmployerPair::getPerson, toList())));
    }

    @Test
    public void greatestExperiencePerEmployer() {
        Map<String, Person> employeesIndex = getEmployees().stream()
                .flatMap(employee -> employee.getJobHistory().stream()
                        .collect(toMap(j -> j.getEmployer(), j -> (employee)))
                        .entrySet()
                        .stream()
                )
                .collect(groupingBy(e -> e.getKey(), mapping(e -> e.getValue(), toList()))).entrySet().stream()
                .collect(toMap(e -> e.getKey(), e -> e.getValue().stream()
                        .collect(maxBy((o1, o2) -> {
                            if (o1.getJobHistory().stream()
                                    .filter(j -> j.getEmployer() == e.getKey())
                                    .collect(toList())
                                    .get(0).getDuration()
                                    >=
                                    o2.getJobHistory().stream()
                                            .filter(j -> j.getEmployer() == e.getKey())
                                            .collect(toList())
                                            .get(0).getDuration())
                                return 1;
                            return -1;
                        })))).entrySet().stream()
                .collect(toMap(e -> e.getKey(), e -> e.getValue().get().getPerson()));
        /*
    1. собрать в емплоер + лист емплои
    2. в листе емплои найти максимум
     */
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
