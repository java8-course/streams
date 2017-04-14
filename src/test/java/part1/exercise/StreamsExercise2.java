package part1.exercise;

import data.Employee;
import data.JobHistoryEntry;
import data.Person;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
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
        Map<String, List<Person>> employersStuffLists = getEmployees().stream()
            .flatMap(employee -> employee.getJobHistory().stream()
                .map(j -> new PersonEmployerPair(employee.getPerson(), j.getEmployer(), j.getDuration())))
            .collect(Collectors.groupingBy(
                PersonEmployerPair::getEmployer,
                mapping(PersonEmployerPair::getPerson, toList())
            ));

        List<Person> personList = new ArrayList<>();
        personList.add(new Person("John", "Doe", 21));
        personList.add(new Person("John", "Doe", 24));
        personList.add(new Person("Bob", "Doe", 27));
        personList.add(new Person("John", "Doe", 30));

        assertEquals(personList, employersStuffLists.get("abc"));
    }

    @Test
    public void indexByFirstEmployer() {
        Map<String, List<Person>> employeesIndex = getEmployees().stream()
            .map(employee -> new PersonEmployerPair(employee.getPerson(),
                                                    employee.getJobHistory().get(0).getEmployer(),
                                                    employee.getJobHistory().get(0).getDuration()))
            .collect(Collectors.groupingBy(
                PersonEmployerPair::getEmployer,
                mapping(PersonEmployerPair::getPerson, toList())
            ));

        List<Person> personList = new ArrayList<>();
        personList.add(new Person("John", "Galt", 20));
        personList.add(new Person("John", "White", 22));
        personList.add(new Person("John", "Galt", 23));
        personList.add(new Person("John", "White", 25));
        personList.add(new Person("John", "Galt", 26));
        personList.add(new Person("John", "White", 28));
        personList.add(new Person("John", "Galt", 29));
        personList.add(new Person("Bob", "White", 31));

        assertEquals(personList, employeesIndex.get("epam"));

    }

    @Test
    public void greatestExperiencePerEmployer() {
        Map<String, Person> employeesIndex = getEmployees().stream()
            .flatMap(employee -> employee.getJobHistory().stream()
            .map(j -> new PersonEmployerPair(employee.getPerson(), j.getEmployer(), j.getDuration())))
            .collect(Collectors.groupingBy(
                PersonEmployerPair::getEmployer,
                Collectors.collectingAndThen(
                    Collectors.maxBy(Comparator.comparingInt(PersonEmployerPair::getDuration)),
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
