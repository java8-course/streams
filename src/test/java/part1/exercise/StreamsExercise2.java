package part1.exercise;

import data.Employee;
import data.JobHistoryEntry;
import data.Person;
import org.junit.Test;

import java.util.*;

import static java.util.stream.Collectors.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class StreamsExercise2 {
    // https://youtu.be/kxgo7Y4cdA8 Сергей Куксенко и Алексей Шипилёв — Через тернии к лямбдам, часть 1
    // https://youtu.be/JRBWBJ6S4aU Сергей Куксенко и Алексей Шипилёв — Через тернии к лямбдам, часть 2

    // https://youtu.be/O8oN4KSZEXE Сергей Куксенко — Stream API, часть 1
    // https://youtu.be/i0Jr2l3jrDA Сергей Куксенко — Stream API, часть 2

    // TODO class PersonEmployerPair


    class PersonEmployerPair {
        private String employer;
        private Person person;

        PersonEmployerPair(String employer, Person person) {
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

    class PersonEmployerDuration extends PersonEmployerPair {
        private int duration;

        PersonEmployerDuration(String employer, Person person, int duration) {
            super(employer, person);
            this.duration = duration;
        }

        int getDuration() {
            return duration;
        }
    }

    @Test
    public void employersStuffLists() {
        Map<String, Set<Person>> employersStuffLists =
                getEmployees().stream()
                        .flatMap(e ->
                                e.getJobHistory()
                                        .stream()
                                        .map(job -> new PersonEmployerPair(job.getEmployer(), e.getPerson()))
                        )
                        .collect(
                                groupingBy(
                                        PersonEmployerPair::getEmployer,
                                        mapping(
                                                PersonEmployerPair::getPerson,
                                                toSet()
                                        )
                                )
                        );

        getEmployees().forEach(
                employee -> {
                    employersStuffLists.forEach(
                            (employer, persons) -> {
                                persons.forEach(p ->
                                        {
                                            if (employee.getPerson().equals(p)) {
                                                assertTrue(employee.getJobHistory()
                                                        .stream()
                                                        .anyMatch(
                                                                job -> job.getEmployer().equals(employer)
                                                        )
                                                );
                                            }
                                        }
                                );
                            }
                    );
                });
    }

    @Test
    public void indexByFirstEmployer() {
        Map<String, Set<Person>> employeesIndex =
                getEmployees().stream()
                        .map(e ->
                                e.getJobHistory()
                                        .stream()
                                        .findFirst()
                                        .map(job -> new PersonEmployerPair(job.getEmployer(), e.getPerson()))
                        )
                        .filter(Optional::isPresent)
                        .map(Optional::get)
                        .collect(
                                groupingBy(
                                        PersonEmployerPair::getEmployer,
                                        mapping(
                                                PersonEmployerPair::getPerson,
                                                toSet()
                                        )
                                )
                        );

        getEmployees()
                .forEach(employee ->
                        employeesIndex.forEach(
                                (employer, persons) -> {
                                    persons.forEach(p ->
                                            {
                                                if (employee.getPerson().equals(p)) {
                                                    assertTrue(
                                                            employee.getJobHistory()
                                                                    .get(0).getEmployer()
                                                                    .equals(employer)
                                                    );
                                                }
                                            }
                                    );
                                }
                        )
                );
    }

    @Test
    public void greatestExperiencePerEmployer() {
        Map<String, Person> employeesIndex =
                getEmployees().stream()
                        .flatMap(e ->
                                e.getJobHistory()
                                        .stream()
                                        .map(job -> new PersonEmployerDuration(
                                                job.getEmployer(),
                                                e.getPerson(),
                                                job.getDuration())
                                        )
                        )
                        .collect(
                                groupingBy(
                                        PersonEmployerDuration::getEmployer,
                                        collectingAndThen(
                                                maxBy(Comparator.comparingInt(PersonEmployerDuration::getDuration)),
                                                ped -> ped.get().getPerson()
                                        )
                                )
                        );

        assertEquals(new Person("John", "Doe", 30), employeesIndex.get("abc"));
        assertEquals(new Person("John", "Doe", 21), employeesIndex.get("yandex"));
        assertEquals(new Person("John", "White", 28), employeesIndex.get("epam"));
        assertEquals(new Person("John", "Galt", 20), employeesIndex.get("google"));
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
                                new JobHistoryEntry(7, "BA", "yandex"),
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
                        )),
                new Employee(
                        new Person("Empty", "JobHistory", 666),
                        Collections.EMPTY_LIST
                ));

    }

}
