package part1.exercise;

import data.Employee;
import data.JobHistoryEntry;
import data.Person;
import org.junit.Test;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.stream.Collectors.*;
import static org.junit.Assert.assertEquals;

public class StreamsExercise2 {

    // https://youtu.be/kxgo7Y4cdA8 Сергей Куксенко и Алексей Шипилёв — Через тернии к лямбдам, часть 1
    // https://youtu.be/JRBWBJ6S4aU Сергей Куксенко и Алексей Шипилёв — Через тернии к лямбдам, часть 2

    // https://youtu.be/O8oN4KSZEXE Сергей Куксенко — Stream API, часть 1
    // https://youtu.be/i0Jr2l3jrDA Сергей Куксенко — Stream API, часть 2

    private static class PersonEmployerPair {
        private final Person person;
        private final String employer;

        private PersonEmployerPair(Person person, String employee) {
            this.person = person;
            this.employer = employee;
        }

        public Person getPerson() {
            return person;
        }

        public String getEmployee() {
            return employer;
        }
    }

    @Test
    public void employersStuffLists() {
        final Stream<PersonEmployerPair> personEmployerPairStream = getEmployees().stream()
                .flatMap(StreamsExercise2::employeeToPersonEmployerPair);

        final Map<String, List<Person>> employersStuffLists = personEmployerPairStream.collect(groupingBy(
                PersonEmployerPair::getEmployee,
                mapping(PersonEmployerPair::getPerson, toList())
        ));

        final List<Person> expectedStuffLists_abc =
                Arrays.asList(new Person("John", "Doe", 21),
                        new Person("John", "Doe", 24),
                        new Person("Bob", "Doe", 27),
                        new Person("John", "Doe", 30));

        assertEquals(expectedStuffLists_abc, employersStuffLists.get("abc"));
    }

    private static Stream<PersonEmployerPair> employeeToPersonEmployerPair(Employee employee) {
        return employee.getJobHistory().stream()
                .map(jobHistoryEntry -> new PersonEmployerPair(employee.getPerson(), jobHistoryEntry.getEmployer()));
    }

    @Test
    public void indexByFirstEmployer() {
        final Stream<PersonEmployerPair> personFirstEmployerPairStream =
                getEmployees().stream()
                .flatMap(employee -> getPersonFirstEmployerStream_findFirst(employee));

        final Map<String, List<Person>> employeesIndex = personFirstEmployerPairStream.collect(
                groupingBy(
                        PersonEmployerPair::getEmployee,
                        mapping(PersonEmployerPair::getPerson, toList())
                ));

        final List<Person> expectedListPersonWithFirstEmployer_yandex =
                Arrays.asList(new Person("John", "Doe", 21),
                        new Person("John", "Doe", 24),
                        new Person("Bob", "Doe", 27),
                        new Person("John", "Doe", 30));

        assertEquals(expectedListPersonWithFirstEmployer_yandex, employeesIndex.get("yandex"));
    }

    private Stream<PersonEmployerPair> getPersonFirstEmployerStream_limit(Employee employee) {
        return employee.getJobHistory().stream().limit(1)
                .map(jobHistoryEntry -> new PersonEmployerPair(employee.getPerson(), jobHistoryEntry.getEmployer()));
    }

    private Stream<PersonEmployerPair> getPersonFirstEmployerStream_findFirst(Employee employee) {
        Optional<PersonEmployerPair> personEmployerPair =
                employee.getJobHistory()
                        .stream()
                        .findFirst()
                        .map(e -> new PersonEmployerPair(employee.getPerson(), e.getEmployer()));

        if (personEmployerPair.isPresent()) {
            return Stream.of(personEmployerPair.get());
        }
        return Stream.empty();
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

        @Override
        public String toString() {
            return person + " " + employer + " " + duration;
        }
    }

    @Test
    public void greatestExperiencePerEmployer() {
        Map<String, Person> employeesIndex = getEmployees().stream()
                .flatMap(e -> e.getJobHistory().stream()
                        .collect(groupingBy(
                                JobHistoryEntry::getEmployer,
                                summingInt(JobHistoryEntry::getDuration)))
                        .entrySet().stream()
                        .map(esd -> new PersonEmployerDuration(e.getPerson(), esd.getKey(), esd.getValue())))
                .collect(groupingBy(
                        PersonEmployerDuration::getEmployer,
                        collectingAndThen(maxBy(Comparator.comparing(PersonEmployerDuration::getDuration)),
                                o -> o.get().getPerson())));

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
                        Arrays.asList(
                                new JobHistoryEntry(2, "BA", "epam"),
                                new JobHistoryEntry(2, "BA", "epam"),
                                new JobHistoryEntry(2, "BA", "epam"),
                                new JobHistoryEntry(2, "BA", "epam")
                        )),
                new Employee(
                        new Person("John", "Galt", 29),
                        Arrays.asList(
                                new JobHistoryEntry(6, "dev", "epam"),
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
