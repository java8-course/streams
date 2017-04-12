package part1.exercise;

import data.Employee;
import data.JobHistoryEntry;
import data.Person;
import org.junit.Test;

import java.util.*;
import java.util.stream.Stream;

import static data.Generator.generateEmployeeList;
import static java.util.Comparator.comparing;
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

        PersonEmployerPair(Person person, String employer) {
            this.person = person;
            this.employer = employer;
        }

        Person getPerson() {
            return person;
        }

        String getEmployer() {
            return employer;
        }
    }

    private static class PersonEmployerDuration {
        private final Person person;
        private final String employer;
        private final int duration;

        PersonEmployerDuration(Person person, String employer, int duration) {
            this.person = person;
            this.employer = employer;
            this.duration = duration;
        }

        Person getPerson() {
            return person;
        }

        String getEmployer() {
            return employer;
        }

        int getDuration() {
            return duration;
        }
    }

    @Test
    public void employersStuffLists() {
        Map<String, List<Person>> employersStuffLists = getEmployees().stream()
                .flatMap(
                        e -> e.getJobHistory()
                                .stream()
                                .map(j -> new PersonEmployerPair(e.getPerson(), j.getEmployer()))
                )
                .collect(groupingBy(
                        PersonEmployerPair::getEmployer,
                        mapping(PersonEmployerPair::getPerson, toList()))
                );

        Map<String, List<Person>> expected = new HashMap<>();
        getEmployees().forEach(
                e -> e.getJobHistory()
                        .forEach(
                                j -> {
                                    String employer = j.getEmployer();
                                    expected.computeIfAbsent(employer, k -> new ArrayList<>());
                                    expected.get(employer).add(e.getPerson());
                                }
                        )
        );

        assertEquals(expected, employersStuffLists);
    }

    @Test
    public void indexByFirstEmployer() {
        Map<String, List<Person>> employeesIndex = getEmployees().stream()
                .flatMap(
                        e -> e.getJobHistory()
                                .stream()
                                .map(j -> new PersonEmployerPair(e.getPerson(), j.getEmployer()))
                                .limit(1)
                )
                .collect(groupingBy(
                        PersonEmployerPair::getEmployer,
                        mapping(PersonEmployerPair::getPerson, toList()))
                );

        Map<String, List<Person>> expected = new HashMap<>();
        getEmployees().forEach(
                e -> {
                    if (!e.getJobHistory().isEmpty()) {
                        String employer = e.getJobHistory().get(0).getEmployer();
                        expected.computeIfAbsent(employer, k -> new ArrayList<>());
                        expected.get(employer).add(e.getPerson());
                    }
                }
        );

        assertEquals(expected, employeesIndex);
    }

    @Test
    public void greatestExperiencePerEmployer() {
        Map<String, Person> employeesIndex = getEmployees().stream()
                .flatMap(
                        e -> e.getJobHistory().stream()
                                .map(j -> new PersonEmployerDuration(e.getPerson(), j.getEmployer(), j.getDuration()))
                )
                .collect(groupingBy(
                        PersonEmployerDuration::getEmployer,
                        collectingAndThen(
                                maxBy(comparing(PersonEmployerDuration::getDuration)), p -> p.get().getPerson()))
                );

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
