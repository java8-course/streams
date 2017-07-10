package part1.exercise;

import data.Employee;
import data.JobHistoryEntry;
import data.Person;
import org.junit.Test;

import java.util.*;
import java.util.function.Function;

import static java.util.stream.Collectors.*;
import static org.junit.Assert.assertEquals;

public class StreamsExercise2 {
    // https://youtu.be/kxgo7Y4cdA8 Сергей Куксенко и Алексей Шипилёв — Через тернии к лямбдам, часть 1
    // https://youtu.be/JRBWBJ6S4aU Сергей Куксенко и Алексей Шипилёв — Через тернии к лямбдам, часть 2

    // https://youtu.be/O8oN4KSZEXE Сергей Куксенко — Stream API, часть 1
    // https://youtu.be/i0Jr2l3jrDA Сергей Куксенко — Stream API, часть 2

    private class PersonEmploerPair {
        private final Person person;
        private final String employer;

        public PersonEmploerPair(final Person person, final String employer) {
            this.person = person;
            this.employer = employer;
        }

        public String getEmployer() {
            return employer;
        }

        public Person getPerson() {
            return person;
        }
    }

    private class PersonEmploerVeryUseful {
        private final Person person;
        private final String employer;
        private int duration;

        public PersonEmploerVeryUseful(final Person person, final String employer, final int duration) {
            this.person = person;
            this.employer = employer;
            this.duration = duration;
        }

        public String getEmployer() {
            return employer;
        }

        public Person getPerson() {
            return person;
        }

        public int getDuration() {
            return duration;
        }

        public void setDuration(int duration) {
            this.duration = duration;
        }
    }

    @Test
    public void employersStuffLists() {
        final Map<String, List<Person>> employersStuffLists = getEmployees()
                .stream()
                .flatMap(e -> e.getJobHistory()
                        .stream()
                        .map(j -> new PersonEmploerPair(e.getPerson(), j.getEmployer())))
                .collect(groupingBy(PersonEmploerPair::getEmployer,
                        mapping(PersonEmploerPair::getPerson, toList())));
        final Map<String, List<Person>> badCalculated = new HashMap<>();
        final List<Employee>            employees     = getEmployees();
        for (Employee e : employees) {
            for (JobHistoryEntry j : e.getJobHistory()) {
                badCalculated.computeIfAbsent(j.getEmployer(), v -> new ArrayList<>()).add(e.getPerson());
            }
        }
        assertEquals(badCalculated, employersStuffLists);
    }

    @Test
    public void indexByFirstEmployer() {
        final Map<String, List<Person>> employeesIndex = new HashMap<>();
        final List<Employee>            employees      = getEmployees();
        for (final Employee e : employees) {
            employeesIndex.computeIfAbsent(e.getJobHistory().get(0).getEmployer(), v -> new ArrayList<>()).add(e.getPerson());
        }

        final Map<String, List<Person>> streamed = employees
                .stream()
                .map(e -> new PersonEmploerPair(e.getPerson(), e.getJobHistory().get(0).getEmployer()))
                .collect(groupingBy(PersonEmploerPair::getEmployer,
                        mapping(PersonEmploerPair::getPerson, toList())));
        assertEquals(employeesIndex, streamed);
    }

    @Test
    public void greatestExperiencePerEmployer() {
        final List<Employee> employees = getEmployees();

        final Map<String, Map<PersonEmploerVeryUseful, IntSummaryStatistics>> collect = employees
                .stream()
                .flatMap(e -> e.getJobHistory()
                        .stream()
                        .map(j -> new PersonEmploerVeryUseful(e.getPerson(), j.getEmployer(), j.getDuration())))
                .collect(groupingBy(PersonEmploerVeryUseful::getEmployer,
                        groupingBy(Function.identity(),
                                summarizingInt(PersonEmploerVeryUseful::getDuration))));

        final Map<String, Person> collect1 = collect.entrySet()
                .stream()
                .flatMap(p -> p.getValue().entrySet()
                        .stream()
                        .sorted((r, l) -> Long.valueOf(l.getValue().getSum()).compareTo(r.getValue().getSum()))
                        .limit(1))
                .map(Map.Entry::getKey)
                .collect(toMap(PersonEmploerVeryUseful::getEmployer, PersonEmploerVeryUseful::getPerson));
        assertEquals(new Person("John", "White", 28), collect1.get("epam"));
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
