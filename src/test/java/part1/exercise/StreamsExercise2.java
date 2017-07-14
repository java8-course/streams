package part1.exercise;

import data.Employee;
import data.JobHistoryEntry;
import data.Person;
import org.junit.Test;

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

        public PersonEmployerDuration (Person person, String employer, int duration) {
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

    private static Stream<PersonEmployerPair> employersToPersons(Employee employee) {
        return employee.getJobHistory().stream()
                .map(JobHistoryEntry::getEmployer)
                .map(person -> new PersonEmployerPair(employee.getPerson(), person));
    }

    @Test
    public void employersStuffLists() {
        List<Employee> employees = generateEmployeeList();

        final Stream<PersonEmployerPair> personEmployerPairStream = employees.stream()
                .flatMap(StreamsExercise2::employersToPersons);

        Map<String, List<Person>> var1 =
                personEmployerPairStream
                        .collect(Collectors.groupingBy(PersonEmployerPair::getEmployer,
                                mapping(PersonEmployerPair::getPerson, toList())));

        Map<String, List<Person>> var2  = new HashMap<>();

        for (Employee employee : employees) {
            for (JobHistoryEntry jobHistoryEntry : employee.getJobHistory()) {
                if (var2.containsKey(jobHistoryEntry.getEmployer())) {
                    var2.get(jobHistoryEntry.getPosition()).add(employee.getPerson());
                } else {
                    var2.put(jobHistoryEntry.getEmployer(),
                            new ArrayList<>(Collections.singletonList(employee.getPerson())));
                }
            }
        }
        assertEquals(var1, var2);
    }

    @Test
    public void indexByFirstEmployer() {
        List<Employee> employees = generateEmployeeList();

        Map<String, List<Person>> var1
                = employees.stream().limit(1)
                .flatMap(employee -> employee.getJobHistory().stream()
                        .map(jobHistoryEntry -> new PersonEmployerPair(employee.getPerson(), jobHistoryEntry.getEmployer())))
                .collect(Collectors.groupingBy(PersonEmployerPair::getEmployer,
                        mapping((PersonEmployerPair::getPerson), toList())));


        Map<String, List<Person>> var2 = new HashMap<>();
        for (Employee e : employees) {
            for (JobHistoryEntry j : e.getJobHistory()) {
                if (var2.containsKey(e.getJobHistory().get(0).getEmployer())) {
                    var2.get(j.getEmployer()).add(e.getPerson());
                } else {
                    var2.put(j.getEmployer(), new ArrayList<>(Collections.singletonList(e.getPerson())));
                }
            }
        }

        assertEquals(var1, var2);
    }

    @Test
    public void greatestExperiencePerEmployer() {
        Map<String, Person> employeesIndex = getEmployees().stream()
                .flatMap(e -> e.getJobHistory().stream()
                        .map(j -> new PersonEmployerDuration(e.getPerson(), j.getEmployer(), j.getDuration())))
                .collect(groupingBy(PersonEmployerDuration::getEmployer,
                        collectingAndThen(maxBy(Comparator.comparing(PersonEmployerDuration::getDuration)),
                                p -> p.get().getPerson())));

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
