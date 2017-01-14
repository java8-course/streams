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

    // TODO class PersonEmployerPair
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

        public PersonEmployerDuration(Person person, String position, int duration) {
            this.person = person;
            this.employer = position;
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

    private static Stream<PersonEmployerPair> employeeToPair(Employee employee) {
        return employee.getJobHistory().stream()
                .map(JobHistoryEntry::getEmployer)
                .map(p -> new PersonEmployerPair(employee.getPerson(), p));
    }

    private static Map<String, List<Person>> getEmployerIndex(List<Employee> employees) {
        final Stream<PersonEmployerPair> personEmployerPairStream = employees.stream()
                .flatMap(StreamsExercise2::employeeToPair);

        return personEmployerPairStream
                .collect(Collectors.groupingBy(
                        PersonEmployerPair::getEmployer,
                        mapping(PersonEmployerPair::getPerson, toList())));
    }


    @Test
    public void indexByFirstEmployer() {
        List<Employee> employees = getEmployees();
        Map<String, List<Person>> employeesIndex = employees.stream().map(
                e -> new PersonEmployerPair(e.getPerson(), e.getJobHistory().get(0).getEmployer()))
                .collect(Collectors.groupingBy(
                        PersonEmployerPair::getEmployer,
                        mapping(PersonEmployerPair::getPerson, toList())
                ));

        Map<String, List<Person>> expected = new HashMap<>();
        for (Employee employee : employees) {
            if (expected.containsKey(employee.getJobHistory().get(0).getEmployer())) {
                List<Person> tempList = expected.get(employee.getJobHistory().get(0).getEmployer());
                tempList.add(employee.getPerson());
                expected.put(employee.getJobHistory().get(0).getEmployer(), tempList);
            } else {
                List<Person> tempList = new ArrayList<>();
                tempList.add(employee.getPerson());
                expected.put(employee.getJobHistory().get(0).getEmployer(), tempList);
            }
        }

        assertEquals(expected, employeesIndex);
    }

    @Test
    public void employersStuffLists() {
        List<Employee> employees = getEmployees();

        Map<String, List<Person>> employersStuffLists = getEmployerIndex(employees);

        Map<String, List<Person>> expected = new HashMap<>();
        for (Employee employee : employees) {
            for (JobHistoryEntry entry : employee.getJobHistory()) {
                if (expected.containsKey(entry.getEmployer())) {
                    List<Person> personList = expected.get(entry.getEmployer());
                    personList.add(employee.getPerson());
                    expected.put(entry.getEmployer(), personList);
                } else {
                    List<Person> personList = new ArrayList<>();
                    personList.add(employee.getPerson());
                    expected.put(entry.getEmployer(), personList);
                }
            }
        }

        assertEquals(expected, employersStuffLists);
    }

    @Test
    public void greatestExperiencePerEmployer() {
        List<Employee> employees = getEmployees();
        Map<String, Person> employeesIndex = employees.stream().flatMap(
                e -> e.getJobHistory().stream().map(
                        j -> new PersonEmployerDuration(e.getPerson(), j.getEmployer(), j.getDuration())))
                .collect(Collectors.groupingBy(
                        PersonEmployerDuration::getEmployer,
                        collectingAndThen(
                                maxBy(Comparator.comparing(PersonEmployerDuration::getDuration)),
                                p -> p.get().getPerson())));

        assertEquals(new Person("John", "White", 28), employeesIndex.get("epam"));
        assertEquals(new Person("John", "Doe", 21), employeesIndex.get("yandex"));
        assertEquals(new Person("John", "Doe", 30), employeesIndex.get("abc"));
        assertEquals(new Person("John", "Galt", 26), employeesIndex.get("google"));
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
                                new JobHistoryEntry(666, "BA", "yandex"),
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
                                new JobHistoryEntry(666, "dev", "google")
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
                                new JobHistoryEntry(666, "dev", "abc")
                        )),
                new Employee(
                        new Person("Bob", "White", 31),
                        Collections.singletonList(
                                new JobHistoryEntry(6, "QA", "epam")
                        ))
        );
    }
}
