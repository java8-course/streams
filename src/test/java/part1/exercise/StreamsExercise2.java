package part1.exercise;

import data.Employee;
import data.JobHistoryEntry;
import data.Person;
import org.junit.Test;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.stream.Collectors.mapping;
import static java.util.stream.Collectors.toSet;
import static org.junit.Assert.assertEquals;

public class StreamsExercise2 {
    // https://youtu.be/kxgo7Y4cdA8 Сергей Куксенко и Алексей Шипилёв — Через тернии к лямбдам, часть 1
    // https://youtu.be/JRBWBJ6S4aU Сергей Куксенко и Алексей Шипилёв — Через тернии к лямбдам, часть 2

    // https://youtu.be/O8oN4KSZEXE Сергей Куксенко — Stream API, часть 1
    // https://youtu.be/i0Jr2l3jrDA Сергей Куксенко — Stream API, часть 2

    // class PersonEmployerPair
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

    // список сотрудников, которые работали у этого работодателя
    @Test
    public void employersStuffLists() {
        List<Employee> employees = getEmployees();
        final Stream<PersonEmployerPair> personEmployerPairStream = employees.stream()
                .flatMap(StreamsExercise2::employeeToPairs);

        Map<String, Set<Person>> collect = personEmployerPairStream.collect(Collectors.groupingBy(
                PersonEmployerPair::getEmployer,
                mapping(PersonEmployerPair::getPerson, toSet())));

        Map<String, Set<Person>> actualEmployersStuffLists = collect;
        Map<String, Set<Person>> expectedEmployersStuffLists = new HashMap<>();

        Set<String> setOfEmployers = getSetOfEmployers();
        for (String employer: setOfEmployers) {
            Set<Person> personSet = new HashSet<>();
            for (Employee employee : employees) {
                List<JobHistoryEntry> jobs = employee.getJobHistory();
                for (JobHistoryEntry entry: jobs) {
                    if (entry.getEmployer().equals(employer)) {
                        personSet.add(employee.getPerson());
                    }
                }
            }
            expectedEmployersStuffLists.put(employer, personSet);
        }
        assertEquals(expectedEmployersStuffLists, actualEmployersStuffLists);
    }

    private static Stream<PersonEmployerPair> employeeToPairs(Employee employee) {
        return employee.getJobHistory().stream()
                .map(JobHistoryEntry::getEmployer)
                .map(p -> new StreamsExercise2.PersonEmployerPair(employee.getPerson(), p));
    }

    private static Stream<PersonEmployerPair> employeeToPairsWithFirst(Employee employee) {
        return employee.getJobHistory().stream()
                .limit(1)
                .map(JobHistoryEntry::getEmployer)
                .map(p -> new StreamsExercise2.PersonEmployerPair(employee.getPerson(), p));
    }

    // список сотрудников, которые начинали работать у этого работодателя
    @Test
    public void indexByFirstEmployer() {
        List<Employee> employees = getEmployees();
        final Stream<PersonEmployerPair> personEmployerPairStream = employees.stream()
                .flatMap(StreamsExercise2::employeeToPairsWithFirst);

        Map<String, Set<Person>> collect = personEmployerPairStream.collect(Collectors.groupingBy(
                PersonEmployerPair::getEmployer,
                mapping(PersonEmployerPair::getPerson, toSet())));

        Map<String, Set<Person>> actualEmployersStuffLists = collect;
        Map<String, Set<Person>> expectedEmployersStuffLists = new HashMap<>();

        Set<String> setOfEmployers = getSetOfEmployers();
        for (String employer: setOfEmployers) {
            Set<Person> personSet = new HashSet<>();
            for (Employee employee : employees) {
                if (employee.getJobHistory().get(0).getEmployer().equals(employer)) {
                    personSet.add(employee.getPerson());
                }
            }
            if (!personSet.isEmpty()) {
                expectedEmployersStuffLists.put(employer, personSet);
            }
        }
        assertEquals(expectedEmployersStuffLists, actualEmployersStuffLists);
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
    // максимальное суммарное время на одного работодателя по одной записи
    @Test
    public void greatestExperiencePerEmployer() {
        List<Employee> employees = getEmployees();
        final Stream<PersonEmployerDuration> personEmployerDurationStream = employees.stream()
                .flatMap(StreamsExercise2::toPersonEmployerDuration);

        Map<String, Person> employeesIndex = personEmployerDurationStream.collect(Collectors.groupingBy(
                PersonEmployerDuration::getEmployer,
                Collectors.collectingAndThen(Collectors.maxBy(
                        Comparator.comparingInt(PersonEmployerDuration::getDuration)),
                        p -> p.get().getPerson()
                )));
        assertEquals(new Person("John", "White", 28), employeesIndex.get("epam"));
    }

    private static Stream<PersonEmployerDuration> toPersonEmployerDuration(Employee employee) {
        return employee.getJobHistory().stream()
                .map(p -> new StreamsExercise2.PersonEmployerDuration(employee.getPerson(), p.getEmployer(), p.getDuration()));
    }

    private Set<String> getSetOfEmployers() {
        Set<String> set = new HashSet<>();
        set.add("epam");
        set.add("google");
        set.add("yandex");
        set.add("abc");
        return set;
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