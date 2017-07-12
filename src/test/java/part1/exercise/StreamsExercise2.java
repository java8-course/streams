package part1.exercise;

import data.Employee;
import data.JobHistoryEntry;
import data.Person;
import org.junit.Test;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class StreamsExercise2 {
    // https://youtu.be/kxgo7Y4cdA8 Сергей Куксенко и Алексей Шипилёв — Через тернии к лямбдам, часть 1
    // https://youtu.be/JRBWBJ6S4aU Сергей Куксенко и Алексей Шипилёв — Через тернии к лямбдам, часть 2

    // https://youtu.be/O8oN4KSZEXE Сергей Куксенко — Stream API, часть 1
    // https://youtu.be/i0Jr2l3jrDA Сергей Куксенко — Stream API, часть 2

    // TODO class PersonEmployerPair
    private static class PersonEmployerPair {
        private Person person;
        private String employer;

        public PersonEmployerPair(Person person, String employer) {
            this.person = person;
            this.employer = employer;
        }

        public Person getPerson() {
            return person;
        }

        public String getEmployer() {
            return employer;
        }
    }

    @Test
    public void employersStuffLists() {
        Stream<PersonEmployerPair> personEmployerPairStream = getEmployees().stream()
                .flatMap(this::toPersonEmployerPair);

        Map<String, Set<Person>> employersStuffLists = personEmployerPairStream
                .collect(Collectors.groupingBy(
                                PersonEmployerPair::getEmployer,
                                Collectors.mapping(PersonEmployerPair::getPerson, Collectors.toSet())));
    }

    private Stream<PersonEmployerPair> toPersonEmployerPair(Employee employee) {
        return employee.getJobHistory().stream()
                .map(jhe -> new PersonEmployerPair(employee.getPerson(), jhe.getEmployer()));
    }

    @Test
    public void indexByFirstEmployer() {
        Stream<PersonEmployerPair> personFirstEmployerPairStream = getEmployees().stream()
                .flatMap(this::toPersonAndFirstEmployerPair);

        Map<String, Set<Person>> employeesIndex = personFirstEmployerPairStream
                .collect(Collectors.groupingBy(
                        PersonEmployerPair::getEmployer,
                        Collectors.mapping(PersonEmployerPair::getPerson, Collectors.toSet())));
    }

    private Stream<PersonEmployerPair> toPersonAndFirstEmployerPair(Employee employee) {
        return employee.getJobHistory().stream()
                .limit(1)
                .map(jhe -> new PersonEmployerPair(employee.getPerson(), jhe.getEmployer()));
    }
    @Test
    public void greatestExperiencePerEmployer() {
        Stream<PersonEmployerPair> personGreatestEmployerPairStream = getEmployees().stream()
                .flatMap(this::toPersonAndFirstEmployerPair);

//        Map<String, List<Person>> employeesIndex = personGreatestEmployerPairStream
//                .collect(Collectors.groupingBy(
//                        PersonEmployerPair::getEmployer,
//                        Collectors.maxBy(Comparator.comparingInt())));

//        assertEquals(new Person("John", "White", 28), employeesIndex.get("epam"));
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
