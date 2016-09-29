package part1.exercise;

import data.Employee;
import data.JobHistoryEntry;
import data.Person;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.*;
import static org.junit.Assert.assertEquals;

public class StreamsExercise2 {
    // https://youtu.be/kxgo7Y4cdA8 Сергей Куксенко и Алексей Шипилёв — Через тернии к лямбдам, часть 1
    // https://youtu.be/JRBWBJ6S4aU Сергей Куксенко и Алексей Шипилёв — Через тернии к лямбдам, часть 2

    // https://youtu.be/O8oN4KSZEXE Сергей Куксенко — Stream API, часть 1
    // https://youtu.be/i0Jr2l3jrDA Сергей Куксенко — Stream API, часть 2

    private static List<Employee> employees = getEmployees();

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

        @Override
        public String toString() {
            return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
                    .append("person", person)
                    .append("employer", employer)
                    .toString();
        }

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

    private static List<Employee> getEmployees() {
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
                                new JobHistoryEntry(400, "dev", "epam"),
                                new JobHistoryEntry(400, "dev", "epam"),
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

    private static Stream<PersonEmployerPair> employeeToPairs(Employee employee) {
        return employee.getJobHistory().stream()
                .map(JobHistoryEntry::getEmployer)
                .map(e -> new PersonEmployerPair(employee.getPerson(), e));
    }

    private static Stream<PersonEmployerPair> firstEmployeeToPairs(Employee employee) {
        return employee.getJobHistory().stream()
                .map(JobHistoryEntry::getEmployer)
                .limit(1)
                .map(e -> new PersonEmployerPair(employee.getPerson(), e));
    }

    @Test
    public void employersStuffLists() {
        System.out.println("Employers list stuff:");
        Map<String, List<Person>> employersStuffLists = employees.stream()
                .flatMap(StreamsExercise2::employeeToPairs)
                .collect(Collectors.groupingBy(
                        PersonEmployerPair::getEmployer,
                        mapping(PersonEmployerPair::getPerson, toList())));

        employersStuffLists.keySet()
                .forEach(k -> {
                    System.out.println(k);
                    employersStuffLists.get(k).forEach(System.out::println);
                })
        ;
    }

    @Test
    public void indexByFirstEmployer() {
        System.out.println("Employers list stuff (index by first):");
        Map<String, List<Person>> employersStuffLists = employees.stream()
                .flatMap(StreamsExercise2::firstEmployeeToPairs)
                .collect(Collectors.groupingBy(
                        PersonEmployerPair::getEmployer,
                        mapping(PersonEmployerPair::getPerson, toList())));

        employersStuffLists.keySet()
                .forEach(k -> {
                    System.out.println(k);
                    employersStuffLists.get(k).forEach(System.out::println);
                })
        ;
    }

    private Map<String, Person> getGreatestExperiencePerEmployer(List<Employee> employees) {

        final Stream<PersonEmployerDuration> personEmployerDurationStream = employees.stream()
                .flatMap(
                        e -> e.getJobHistory().stream()
                                .collect(groupingBy(JobHistoryEntry::getEmployer, summingInt(JobHistoryEntry::getDuration)))
                                .entrySet().stream()
                                .map(es -> new PersonEmployerDuration(e.getPerson(), es.getKey(), es.getValue())));

        return personEmployerDurationStream
                .collect(groupingBy(
                        PersonEmployerDuration::getEmployer,
                        collectingAndThen(
                                maxBy(comparing(PersonEmployerDuration::getDuration)), p -> p.get().getPerson())));
    }

    @Test
    public void greatestExperiencePerEmployer() {
        Map<String, Person> employeesIndex = getGreatestExperiencePerEmployer(getEmployees());

        assertEquals(new Person("John", "Galt", 26), employeesIndex.get("epam"));
    }

}
