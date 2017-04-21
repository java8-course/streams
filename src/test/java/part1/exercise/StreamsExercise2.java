package part1.exercise;

import data.Employee;
import data.JobHistoryEntry;
import data.Person;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static data.Generator.generateEmployeeList;
import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.*;
import static org.junit.Assert.assertEquals;

public class StreamsExercise2 {// https://youtu.be/kxgo7Y4cdA8 Сергей Куксенко и Алексей Шипилёв — Через тернии к лямбдам, часть 1
    // https://youtu.be/JRBWBJ6S4aU Сергей Куксенко и Алексей Шипилёв — Через тернии к лямбдам, часть 2

    // https://youtu.be/O8oN4KSZEXE Сергей Куксенко — Stream API, часть 1
    // https://youtu.be/i0Jr2l3jrDA Сергей Куксенко — Stream API, часть 2

    private static class PersonEmployerPair {

        private final Person person;
        private final String employer;

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

    private static Stream<PersonEmployerPair> employeeToPairs(Employee employee) {
        return employee.getJobHistory().stream()
            .map(JobHistoryEntry::getEmployer)
            .map(e -> new PersonEmployerPair(employee.getPerson(), e));
    }

    @Test
    public void employersStuffLists() {

        final Stream<PersonEmployerPair> personEmployerPairStream = getEmployees().stream()
            .flatMap(StreamsExercise2::employeeToPairs);

        Map<String, List<Person>> employersStuffLists = personEmployerPairStream
            .collect(
                HashMap::new,
                (m, p) -> {
                    final List<Person> list = m.computeIfAbsent(p.getEmployer(), (k) -> new ArrayList<>());
                    list.add(p.getPerson());
                },
                (m1, m2) -> {
                    for (Map.Entry<String, List<Person>> entry : m2.entrySet()) {
                        List<Person> list = m1.computeIfAbsent(entry.getKey(),
                                                               (k) -> new ArrayList<>());
                        list.addAll(entry.getValue());
                    }
                }
            );

        final Map<String, List<Person>> expectedList = personEmployerPairStream.collect(
            Collectors.groupingBy
                (PersonEmployerPair::getEmployer,
                 Collectors.mapping(PersonEmployerPair::getPerson, Collectors.toList())));

        assertEquals(employersStuffLists, expectedList);
    }

    private static PersonEmployerPair firstEmployerPersonPair(Employee employee) {
        final JobHistoryEntry jobHistoryEntry = employee.getJobHistory().stream()
            .findFirst()
            .get();

        return new PersonEmployerPair(employee.getPerson(), jobHistoryEntry.getEmployer());
    }

    @Test
    public void indexByFirstEmployer() {
        Map<String, List<Person>> employeesIndex = new HashMap<>();
        final List<Employee> employees = getEmployees();
        for (Employee employee : employees) {
            for (JobHistoryEntry jobHistoryEntry : employee.getJobHistory()) {
                employeesIndex.put(jobHistoryEntry.getEmployer(), new ArrayList<>());
            }
        }
        for (Employee employee : employees) {
            employeesIndex.get(employee.getJobHistory().get(0).getEmployer()).add(employee.getPerson());
        }

        employeesIndex.entrySet().removeIf(entry -> entry.getValue().equals(Collections.EMPTY_LIST));

        final Stream<PersonEmployerPair> personEmployerPairStream = employees.stream()
            .map(StreamsExercise2::firstEmployerPersonPair);

        final Map<String, List<Person>> expected = personEmployerPairStream
            .collect(Collectors.groupingBy
                (PersonEmployerPair::getEmployer,
                 Collectors.mapping(PersonEmployerPair::getPerson, Collectors.toList())));

        assertEquals(employeesIndex, expected);
    }

    private static class PersonEmployerDuration {

        private final Person person;
        private final String employer;
        private final int duration;

        public PersonEmployerDuration(Person person, String employer, int duration) {
            this.person = person;
            this.employer = employer;
            this.duration = duration;
        }

        public Person getPerson() {
            return person;
        }

        public String getEmployer() {
            return employer;
        }

        public int getDuration() {
            return duration;
        }
    }

    @Test
    public void greatestExperiencePerEmployer() {
        Map<String, Person> employeesIndex = null;
        final List<Employee> employees = getEmployees();
        final Stream<PersonEmployerDuration> personEmployerDurationStream = employees.stream()
            .flatMap(
                e -> e.getJobHistory()
                    .stream()
                    .map(j -> new PersonEmployerDuration(e.getPerson(), j.getEmployer(), j.getDuration())));

        employeesIndex = personEmployerDurationStream
            .collect(groupingBy(
                PersonEmployerDuration::getEmployer,
                collectingAndThen(
                    maxBy(comparing(PersonEmployerDuration::getDuration)), p -> p.get().getPerson())));

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
