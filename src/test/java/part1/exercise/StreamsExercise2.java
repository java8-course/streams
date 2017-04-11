package part1.exercise;

import data.Employee;
import data.JobHistoryEntry;
import data.Person;
import org.junit.Test;

import java.util.*;
import java.util.stream.Stream;

import static java.util.stream.Collectors.*;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

public class StreamsExercise2 {
    // https://youtu.be/kxgo7Y4cdA8 Сергей Куксенко и Алексей Шипилёв — Через тернии к лямбдам, часть 1
    // https://youtu.be/JRBWBJ6S4aU Сергей Куксенко и Алексей Шипилёв — Через тернии к лямбдам, часть 2

    // https://youtu.be/O8oN4KSZEXE Сергей Куксенко — Stream API, часть 1
    // https://youtu.be/i0Jr2l3jrDA Сергей Куксенко — Stream API, часть 2

    // TODO class PersonEmployerPair
    public static class PersonEmployerPair {
        private Person person;
        private String employer;
        private int duration;

        public PersonEmployerPair(Person person, String employer) {
            this.person = person;
            this.employer = employer;
        }

        public PersonEmployerPair(Person person, String employer, int duration) {
            this(person, employer);
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
    public void employersStuffLists() {
        List<Employee> employees = getEmployees();

        Map<String, List<Person>> expected = new HashMap<>();
        for (Employee employee : employees) {
            for (JobHistoryEntry j : employee.getJobHistory()) {
                if (!expected.containsKey(j.getEmployer())) {
                    expected.put(j.getEmployer(), new ArrayList<Person>());
                }
                expected.get(j.getEmployer()).add(employee.getPerson());
            }
        }

        Map<String, List<Person>> actual = employees.stream()
                .flatMap(this::getPersonEmployerPairs)
                .collect(groupingBy(
                        PersonEmployerPair::getEmployer,
                        mapping(PersonEmployerPair::getPerson, toList())
                ));

        assertThat(actual, equalTo(expected));
    }

    private Stream<PersonEmployerPair> getPersonEmployerPairs(Employee employee) {
        return employee.getJobHistory().stream()
                .map(j -> new PersonEmployerPair(employee.getPerson(), j.getEmployer()));
    }

    @Test
    public void indexByFirstEmployer() {
        List<Employee> employees = getEmployees();

        Map<String, List<Person>> expected = new HashMap<>();
        for (Employee employee : employees) {
            if (employee.getJobHistory().size() > 0) {
                String employer = employee.getJobHistory().get(0).getEmployer();
                if (!expected.containsKey(employer)) {
                    expected.put(employer, new ArrayList<>());
                }

                expected.get(employer).add(employee.getPerson());
            }
        }

        Map<String, List<Person>> actual = employees.stream()
                .flatMap(this::getFirstPersonEmployerPairs)
                .collect(groupingBy(
                        PersonEmployerPair::getEmployer,
                        mapping(PersonEmployerPair::getPerson, toList())
                ));


        assertThat(actual, equalTo(expected));
    }

    private Stream<PersonEmployerPair> getFirstPersonEmployerPairs(Employee employee) {
        return employee.getJobHistory().stream()
                .map(j -> new PersonEmployerPair(employee.getPerson(), j.getEmployer()))
                .limit(1);
    }

    @Test
    public void greatestExperiencePerEmployer() {
        List<Employee> employees = getEmployees();


        Map<String, Person> employeesIndex = employees.stream()
                .flatMap(this::getPersonEmployerDuration)
                .collect(groupingBy(
                        PersonEmployerPair::getEmployer,
                        collectingAndThen(
                                maxBy(Comparator.comparingInt(PersonEmployerPair::getDuration)),
                                p -> p.get().getPerson()
                        )
                ));

        assertEquals(new Person("John", "White", 28), employeesIndex.get("epam"));
    }

    private Stream<PersonEmployerPair> getPersonEmployerDuration(Employee employee) {
        return employee.getJobHistory().stream()
                .map(j -> new PersonEmployerPair(employee.getPerson(), j.getEmployer(), j.getDuration()));
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
