package part1.exercise;

import data.Employee;
import data.JobHistoryEntry;
import data.Person;
import org.junit.Assert;
import org.junit.Test;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.stream.Collectors.*;

public class StreamsExercise2 {
    // https://youtu.be/kxgo7Y4cdA8 Сергей Куксенко и Алексей Шипилёв — Через тернии к лямбдам, часть 1
    // https://youtu.be/JRBWBJ6S4aU Сергей Куксенко и Алексей Шипилёв — Через тернии к лямбдам, часть 2

    // https://youtu.be/O8oN4KSZEXE Сергей Куксенко — Stream API, часть 1
    // https://youtu.be/i0Jr2l3jrDA Сергей Куксенко — Stream API, часть 2

    @Test
    public void employersStuffLists() {
        Map<String, List<Person>> employersStuffLists = getEmployees()
                .stream()
                .flatMap(StreamsExercise2::employeeToPairs)
                .collect(Collectors.groupingBy(EmployeerPersonPair::getEmployeer, mapping(EmployeerPersonPair::getPerson, toList())));

        Map<String, List<Person>> expected = new HashMap<>();
        for (Employee e: getEmployees()) {
            for (JobHistoryEntry j: e.getJobHistory()) {
                List<Person> tmp;
                if (expected.containsKey(j.getEmployer())) {
                    tmp = expected.get(j.getEmployer());
                    tmp.add(e.getPerson());
                    expected.put(j.getEmployer(), tmp);
                } else {
                    tmp = new ArrayList<>();
                    tmp.add(e.getPerson());
                    expected.put(j.getEmployer(), tmp);
                }
            }
        }

        Assert.assertEquals(expected, employersStuffLists);
    }


    @Test
    public void indexByFirstEmployer() {
        Map<String, List<Person>> employeesIndex = getEmployees()
                .stream()
                .map(e -> new EmployeerPersonPair(e.getPerson(), e.getJobHistory().get(0).getEmployer()))
                .collect(Collectors.groupingBy(EmployeerPersonPair::getEmployeer, mapping(EmployeerPersonPair::getPerson, toList())));

        Map<String, List<Person>> expected = new HashMap<>();
        for (Employee e: getEmployees()) {
            List<Person> tmp;

                if (expected.containsKey(e.getJobHistory().get(0).getEmployer())) {
                    tmp = expected.get(e.getJobHistory().get(0).getEmployer());
                    tmp.add(e.getPerson());
                    expected.put(e.getJobHistory().get(0).getEmployer(), tmp);
                } else {
                    tmp = new ArrayList<>();
                    tmp.add(e.getPerson());
                    expected.put(e.getJobHistory().get(0).getEmployer(), tmp);
                }
            }
        Assert.assertEquals(expected, employeesIndex);
    }
    
    @Test
    public void greatestExperiencePerEmployer() {
        Map<String, Person> employeesIndex = greatestExperience();

        Assert.assertEquals(new Person("John", "White", 28), employeesIndex.get("epam"));
    }

    private Map<String, Person> greatestExperience() {
        final Stream<PersonEmployeerDuration> personEmployeerDurationStream = getEmployees().stream()
                .flatMap(e -> e.getJobHistory()
                        .stream()
                        .map(j -> new PersonEmployeerDuration(e.getPerson(), j.getEmployer(), j.getDuration())));
        Map<String, Person> collect = personEmployeerDurationStream.collect(groupingBy(
                PersonEmployeerDuration::getEmployeer,
                collectingAndThen(
                        maxBy(Comparator.comparing(PersonEmployeerDuration::getDuration)),
                        e -> e.get().person)));
        return collect;
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

    private static class EmployeerPersonPair {
        private final Person person;
        private final String employeer;

        EmployeerPersonPair(Person person, String employeer) {
            this.person = person;
            this.employeer = employeer;
        }

        public Person getPerson() {
            return person;
        }

        public String getEmployeer() {
            return employeer;
        }

    }

    private static Stream<EmployeerPersonPair> employeeToPairs(Employee employee) {
        return employee.getJobHistory().stream()
                .map(JobHistoryEntry::getEmployer)
                .map(p -> new EmployeerPersonPair(employee.getPerson(), p));
    }

    private static class PersonEmployeerDuration {
        private final Person person;
        private final String employeer;
        private final int duration;

        public PersonEmployeerDuration(Person person, String employeer, int duration) {
            this.person = person;
            this.employeer = employeer;
            this.duration = duration;
        }

        public Person getPerson() {
            return person;
        }

        public String getEmployeer() {
            return employeer;
        }

        public int getDuration() {
            return duration;
        }
    }

    }
