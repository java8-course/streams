package part1.exercise;

import data.Employee;
import data.JobHistoryEntry;
import data.Person;
import org.junit.Test;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collector;
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
    private static class PersonEmployeePair {
        private final Person person;
        private final String employer;

        public PersonEmployeePair(Person person, String employer) {
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

    private static class PersonDurationPair {
        private final Person person;
        private final int duration;

        public PersonDurationPair(Person person, int duration) {
            this.person = person;
            this.duration = duration;
        }

        public Person getPerson() {
            return person;
        }

        public int getDuration() {
            return duration;
        }
    }

    private static class PersonEmployeeDurationTriple {
        private final Person person;
        private final String employer;
        private final Integer duration;

        public PersonEmployeeDurationTriple(Person person, String employer, Integer duration) {
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

        public Integer getDuration() {
            return duration;
        }
    }

    @Test
    public void employersStuffLists() {
//        Map<String, List<Person>> employersStuffLists = null;// TODO
        List<Employee> employees = getEmployees();
        Map<String, List<Person>> v1 = employees
                .stream()
                .flatMap(e -> e.getJobHistory()
                        .stream()
                        .map(j -> new PersonEmployeePair(e.getPerson(), j.getEmployer()))
                ).collect(Collectors.groupingBy(
                        PersonEmployeePair::getEmployer,
                        mapping(PersonEmployeePair::getPerson, toList()))
                );

        Map<String, List<Person>> v2 = new HashMap<>();
        for (Employee employee : employees) {
            Person person = employee.getPerson();
            for (JobHistoryEntry jobHistoryEntry : employee.getJobHistory()) {
                String employer = jobHistoryEntry.getEmployer();
                if (v2.containsKey(employer)) {
                    List<Person> people = new ArrayList<>(v2.get(employer));
                    if (!people.contains(person)) {
                        people.add(person);
                    }
                    v2.put(employer, people);
                } else {
                    v2.put(employer, Collections.singletonList(person));
                }
            }
        }

        assertEquals(v1, v2);
//    throw new UnsupportedOperationException();
    }

    @Test
    public void indexByFirstEmployer() {
//        Map<String, List<Person>> employeesIndex = null;// TODO
        List<Employee> employees = getEmployees();
        Map<String, List<Person>> v1 = employees
                .stream()
                .flatMap(e -> e.getJobHistory()
                        .stream()
                        .limit(1)
                        .map(j -> new PersonEmployeePair(e.getPerson(), j.getEmployer()))
                ).collect(Collectors.groupingBy(
                        PersonEmployeePair::getEmployer,
                        mapping(PersonEmployeePair::getPerson, toList()))
                );

        Map<String, List<Person>> v2 = new HashMap<>();
        for (Employee employee : employees) {
            Person person = employee.getPerson();
            JobHistoryEntry jobHistoryEntry = employee.getJobHistory().get(0);
            String employer = jobHistoryEntry.getEmployer();
            if (v2.containsKey(employer)) {
                List<Person> people = new ArrayList<>(v2.get(employer));
                if (!people.contains(person)) {
                    people.add(person);
                }
                v2.put(employer, people);
            } else {
                v2.put(employer, Collections.singletonList(person));
            }
        }

        assertEquals(v1, v2);
//        throw new UnsupportedOperationException();
    }

    @Test
    public void greatestExperiencePerEmployer() {
//        Map<String, Person> employeesIndex = null;// TODO
        List<Employee> employees = getEmployees();
        Map<String, Person> employeesIndex = employees
                .stream()
                .flatMap(e -> e.getJobHistory()
                        .stream()
                        .map(j -> new PersonEmployeeDurationTriple(e.getPerson(), j.getEmployer(), j.getDuration()))
                )
                .collect(
                        Collectors.groupingBy(
                                PersonEmployeeDurationTriple::getEmployer,
                                collectingAndThen(
                                        maxBy(Comparator.comparing(PersonEmployeeDurationTriple::getDuration)),
                                        (p) -> p.get().getPerson()
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
