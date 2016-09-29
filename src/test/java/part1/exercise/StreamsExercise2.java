package part1.exercise;

import data.Employee;
import data.JobHistoryEntry;
import data.Person;
import org.junit.Assert;
import org.junit.Test;
import part1.example.StreamsExample;

import java.util.*;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.Assert.assertEquals;

public class StreamsExercise2 {
    private Collector<PersonEmployerDuration, ?, Map<String, Person>> collectorByMaxExperienceFerEmployer;
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

        final Map<String, List<Person>> employersStuffLists = personEmployerPairStream.collect(Collectors.groupingBy(
                PersonEmployerPair::getEmployee,
                Collectors.mapping(PersonEmployerPair::getPerson, Collectors.toList())
        ));

        final List<Person> expectedStuffLists_abc =
                Arrays.asList(new Person("John", "Doe", 21),
                            new Person("John", "Doe", 24),
                            new Person("Bob", "Doe", 27),
                            new Person("John", "Doe", 30));

        Assert.assertEquals(expectedStuffLists_abc, employersStuffLists.get("abc"));
    }

    private static Stream<PersonEmployerPair> employeeToPersonEmployerPair(Employee employee) {
        return employee.getJobHistory().stream()
                .map(jobHistoryEntry -> new PersonEmployerPair(employee.getPerson(), jobHistoryEntry.getEmployer()));
    }

    @Test
    public void indexByFirstEmployer() {
        final Stream<PersonEmployerPair> personFirstEmployerPairStream = getEmployees().stream()
                .map(e -> new PersonEmployerPair(e.getPerson(), e.getJobHistory().get(0).getEmployer()));

        final Map<String, List<Person>> employeesIndex = personFirstEmployerPairStream.collect(
                Collectors.groupingBy(
                        PersonEmployerPair::getEmployee,
                        Collectors.mapping(PersonEmployerPair::getPerson, Collectors.toList())
                ));

        final List<Person> expectedListPersonWithFirstEmployer_yandex =
                Arrays.asList(new Person("John", "Doe", 21),
                        new Person("John", "Doe", 24),
                        new Person("Bob", "Doe", 27),
                        new Person("John", "Doe", 30));

        Assert.assertEquals(expectedListPersonWithFirstEmployer_yandex, employeesIndex.get("yandex"));
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
        Stream<PersonEmployerDuration> personEmployerDurationStream = getEmployees().stream()
                .flatMap(employee -> getPersonEmployerDurationStream(employee));

        collectorByMaxExperienceFerEmployer = Collectors.groupingBy(
                PersonEmployerDuration::getEmployer,
                Collectors.collectingAndThen(
                        Collectors.maxBy(Comparator.comparing(PersonEmployerDuration::getDuration)),
                        p -> p.get().getPerson()));

        Map<String, Person> employeesIndex =
                personEmployerDurationStream.collect(
                        collectorByMaxExperienceFerEmployer);

        assertEquals(new Person("John", "White", 28), employeesIndex.get("epam"));
    }

    private Stream<PersonEmployerDuration> getPersonEmployerDurationStream(Employee employee) {
        return employee.getJobHistory().stream()
                .map(j -> new PersonEmployerDuration(
                        employee.getPerson(),
                        j.getEmployer(),
                        j.getDuration()
                ));
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
