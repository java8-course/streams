package part1.exercise;

import data.Employee;
import data.JobHistoryEntry;
import data.Person;
import org.junit.Test;

import java.lang.reflect.Array;
import java.util.*;
import java.util.stream.Stream;

import static data.Generator.generateEmployeeList;
import static java.util.stream.Collectors.*;
import static org.junit.Assert.assertEquals;
import static part1.exercise.StreamsExercise2.PersonEmployerDuration.fromJobHistoryAndEmployee;

public class StreamsExercise2 {
    // https://youtu.be/kxgo7Y4cdA8 Сергей Куксенко и Алексей Шипилёв — Через тернии к лямбдам, часть 1
    // https://youtu.be/JRBWBJ6S4aU Сергей Куксенко и Алексей Шипилёв — Через тернии к лямбдам, часть 2

    // https://youtu.be/O8oN4KSZEXE Сергей Куксенко — Stream API, часть 1
    // https://youtu.be/i0Jr2l3jrDA Сергей Куксенко — Stream API, часть 2

    // TODO class PersonEmployerPair

    static class PersonEmployerPair {
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
        List<Employee> employees = getEmployees();

        Map<String, List<Person>> employersStuffLists = employees.stream()
                .flatMap(this::toPersonEmployerPair)
                .collect(groupingBy(
                        PersonEmployerPair::getEmployer, mapping(PersonEmployerPair::getPerson, toList())
                        )
                );

        List<Person> expected = Arrays.asList(
                new Person("John", "Galt", 20),
                new Person("John", "Galt", 23),
                new Person("John", "Galt", 26),
                new Person("John", "Galt", 29)
        );

        assertEquals(expected, employersStuffLists.get("google"));
    }

    private Stream<? extends PersonEmployerPair> toPersonEmployerPair(Employee employee) {
        return employee.getJobHistory().stream()
                .map(jobHistoryEntry -> new PersonEmployerPair(employee.getPerson(), jobHistoryEntry.getEmployer()));
    }

    @Test
    public void indexByFirstEmployer() {
        Map<String, List<Person>> employeesIndex = getEmployees().stream()
                .filter(employee -> employee.getJobHistory().size() > 0)
                .map(
                        employee -> new PersonEmployerPair(employee.getPerson(), employee.getJobHistory().get(0).getEmployer())
                )
                .collect(
                        groupingBy(
                                PersonEmployerPair::getEmployer,
                                mapping(PersonEmployerPair::getPerson, toList())
                        )
                );

        List<Person> expected = Arrays.asList(
                new Person("John", "Galt", 20),
                new Person("John", "White", 22),
                new Person("John", "Galt", 23),
                new Person("John", "White", 25),
                new Person("John", "Galt", 26),
                new Person("John", "White", 28),
                new Person("John", "Galt", 29),
                new Person("Bob", "White", 31)
        );

        assertEquals(expected,employeesIndex.get("epam"));

    }

    static class PersonEmployerDuration {
        private Person person;
        private String employer;
        private int duration;

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

        public static PersonEmployerDuration fromJobHistoryAndEmployee(Employee employee, JobHistoryEntry jobHistoryEntry) {
            return new PersonEmployerDuration(employee.getPerson(), jobHistoryEntry.getEmployer(), jobHistoryEntry.getDuration());
        }

    }

    @Test
    public void greatestExperiencePerEmployer() {
        List<Employee> employees = getEmployees();

        Map<String, Person> employeesIndex = employees.stream()
                .flatMap(this::toPersonEmployerDuration)
                .collect(
                        groupingBy(
                                PersonEmployerDuration::getEmployer,
                                collectingAndThen(
                                        maxBy(Comparator.comparing(PersonEmployerDuration::getDuration)),
                                        p -> p.get().getPerson()
                                )
                        )
                );

        assertEquals(new Person("John", "White", 28), employeesIndex.get("epam"));
    }

    private Stream<? extends PersonEmployerDuration> toPersonEmployerDuration(Employee employee) {
        return employee.getJobHistory().stream()
                .map(jobHistoryEntry -> fromJobHistoryAndEmployee(employee, jobHistoryEntry));
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
