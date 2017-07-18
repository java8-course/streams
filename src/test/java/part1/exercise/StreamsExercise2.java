package part1.exercise;

import data.Employee;
import data.Generator;
import data.JobHistoryEntry;
import data.Person;
import org.junit.Test;
import part1.example.StreamsExample;

import java.util.*;
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

    private static class PersonPositionPair {
        private final Person person;
        private final String position;

        public PersonPositionPair(Person person, String position) {
            this.person = person;
            this.position = position;
        }

        public Person getPerson() {
            return person;
        }

        public String getPosition() {
            return position;
        }
    }

    private static class PersonEmployerPair {

        private final Person person;
        private final String employer;
        private final int duration;

        private PersonEmployerPair(Person person, String employer, int duration) {
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
    public void employersStuffLists() {
        final List<Employee> employees = Generator.generateEmployeeList();

        Map<String, List<Person>> employersStuffLists = employees.stream()
                .flatMap(employee -> employee.getJobHistory().stream()
                        .map(JobHistoryEntry::getPosition)
                        .map(s -> new PersonPositionPair(employee.getPerson(), s)))
                .collect(groupingBy(PersonPositionPair::getPosition, mapping(PersonPositionPair::getPerson, toList())));

        Map<String, List<Person>> res = new HashMap<>();

        for (Employee employee : employees) {
            for (JobHistoryEntry jobHistoryEntry : employee.getJobHistory()) {
                if (res.containsKey(jobHistoryEntry.getPosition()))
                    res.get(jobHistoryEntry.getPosition()).add(employee.getPerson());
                else
                    res.put(jobHistoryEntry.getPosition(), new ArrayList<>(Collections.singletonList(employee.getPerson())));
            }
        }

        assertEquals(res, employersStuffLists);
    }

    @Test
    public void indexByFirstEmployer() {
        final List<Employee> employees = Generator.generateEmployeeList();

        Map<String, List<Person>> employeesIndex = employees.stream()
                .flatMap(employee -> employee.getJobHistory().stream()
                        .limit(1)
                        .map(JobHistoryEntry::getPosition)
                        .map(s -> new PersonPositionPair(employee.getPerson(), s)))
                .collect(groupingBy(PersonPositionPair::getPosition, mapping(PersonPositionPair::getPerson, toList())));

        Map<String, List<Person>> res = new HashMap<>();

        for (Employee employee : employees) {
            List<JobHistoryEntry> jobHistory = employee.getJobHistory();
            for (JobHistoryEntry jobHistoryEntry : jobHistory) {
                if (res.containsKey(jobHistoryEntry.getPosition()))
                    res.get(jobHistoryEntry.getPosition()).add(employee.getPerson());
                else
                    res.put(jobHistoryEntry.getPosition(), new ArrayList<>(Collections.singletonList(employee.getPerson())));
                break;
            }
        }

        assertEquals(res, employeesIndex);

    }

    @Test
    public void greatestExperiencePerEmployer() {
        final List<Employee> employees = getEmployees();

        Map<String, Person> employeesIndex = employees.stream().flatMap(employee -> employee.getJobHistory().stream()
                .map(j -> new PersonEmployerPair(employee.getPerson(), j.getEmployer(), j.getDuration())))
                .collect(Collectors.groupingBy(PersonEmployerPair::getEmployer,
                        Collectors.collectingAndThen(Collectors.maxBy(Comparator.comparingInt(PersonEmployerPair::getDuration)),
                                p -> p.get().getPerson())
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
