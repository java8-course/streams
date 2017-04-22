package part1.exercise;

import data.Employee;
import data.JobHistoryEntry;
import data.Person;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.collectingAndThen;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.mapping;
import static java.util.stream.Collectors.maxBy;
import static java.util.stream.Collectors.toList;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

public class StreamsExercise2 {
    // https://youtu.be/kxgo7Y4cdA8 Сергей Куксенко и Алексей Шипилёв — Через тернии к лямбдам, часть 1
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

    private Stream<PersonEmployerPair> getEmployeePairs(Employee employee) {
        return employee.getJobHistory().stream()
                .map(JobHistoryEntry::getEmployer)
                .map(empl -> new PersonEmployerPair(employee.getPerson(), empl));
    }

    @Test
    public void employersStuffLists() {
        final List<Employee> employees = getEmployees();

        Map<String, List<Person>> result = employees.stream()
                .flatMap(this::getEmployeePairs)
                .collect(Collectors.groupingBy(
                        PersonEmployerPair::getEmployer,
                        mapping(PersonEmployerPair::getPerson, toList())));

        Map<String, List<Person>> expected = new HashMap<>();
        expected.put("abc", Arrays.asList(
                new Person("John", "Doe", 21),
                new Person("John", "Doe", 24),
                new Person("Bob", "Doe", 27),
                new Person("John", "Doe", 30)
        ));
        expected.put("epam", Arrays.asList(
                new Person("John", "Galt", 20),
                new Person("John", "Doe", 21),
                new Person("John", "White", 22),
                new Person("John", "Galt", 23),
                new Person("John", "Doe", 24),
                new Person("John", "White", 25),
                new Person("John", "Galt", 26),
                new Person("Bob", "Doe", 27),
                new Person("John", "White", 28),
                new Person("John", "Galt", 29),
                new Person("John", "Doe", 30),
                new Person("Bob", "White", 31)
        ));
        expected.put("google", Arrays.asList(
                new Person("John", "Galt", 20),
                new Person("John", "Galt", 23),
                new Person("John", "Galt", 26),
                new Person("John", "Galt", 29)
        ));
        expected.put("yandex", Arrays.asList(
                new Person("John", "Doe", 21),
                new Person("John", "Doe", 24),
                new Person("Bob", "Doe", 27),
                new Person("John", "Doe", 30)
        ));

        assertThat(result.get("abc"), is(expected.get("abc")));
        assertThat(result.get("epam"), is(expected.get("epam")));
        assertThat(result.get("google"), is(expected.get("google")));
        assertThat(result.get("yandex"), is(expected.get("yandex")));
    }

    @Test
    public void indexByFirstEmployer() {
        final List<Employee> employees = getEmployees();

        final Map<String, List<Person>> result = employees.stream()
                .map(e -> {
                    final JobHistoryEntry firstJob = e.getJobHistory().get(0);
                    return new PersonEmployerDuration(
                            e.getPerson(),
                            firstJob.getEmployer(), firstJob.getDuration());
                })
                .collect(groupingBy(PersonEmployerDuration::getEmployer,
                        mapping(PersonEmployerDuration::getPerson, toList())));

        Map<String, List<Person>> expected = new HashMap<>();
        expected.put("yandex", Arrays.asList(
                new Person("John", "Doe", 21),
                new Person("John", "Doe", 24),
                new Person("Bob", "Doe", 27),
                new Person("John", "Doe", 30))
        );

        expected.put("epam", Arrays.asList(
                new Person("John", "Galt", 20),
                new Person("John", "White", 22),
                new Person("John", "Galt", 23),
                new Person("John", "White", 25),
                new Person("John", "Galt", 26),
                new Person("John", "White", 28),
                new Person("John", "Galt", 29),
                new Person("Bob", "White", 31))
        );

        assertThat(result.get("abc"), equalTo(null));
        assertThat(result.get("yandex"), is(expected.get("yandex")));
        assertThat(result.get("epam"), is(expected.get("epam")));
    }

    @Test
    public void greatestExperiencePerEmployer() {
        final List<Employee> employees = getEmployees();

        final Map<String, Person> employeesIndex = employees.stream()
                .flatMap(
                        e -> e.getJobHistory()
                                .stream()
                                .map(j -> new PersonEmployerDuration(e.getPerson(), j.getEmployer(), j.getDuration())))
                .collect(groupingBy(
                        PersonEmployerDuration::getEmployer,
                        collectingAndThen(
                                maxBy(comparing(PersonEmployerDuration::getDuration)), p -> p.get().getPerson()
                        )
                ));

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
