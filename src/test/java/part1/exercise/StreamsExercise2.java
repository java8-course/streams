package part1.exercise;

import data.Employee;
import data.JobHistoryEntry;
import data.Person;
import org.junit.Test;
import data.Job;

import java.util.*;

import static java.util.stream.Collectors.*;
import static org.junit.Assert.assertEquals;

public class StreamsExercise2 {
    // https://youtu.be/kxgo7Y4cdA8 Сергей Куксенко и Алексей Шипилёв — Через тернии к лямбдам, часть 1
    // https://youtu.be/JRBWBJ6S4aU Сергей Куксенко и Алексей Шипилёв — Через тернии к лямбдам, часть 2

    // https://youtu.be/O8oN4KSZEXE Сергей Куксенко — Stream API, часть 1
    // https://youtu.be/i0Jr2l3jrDA Сергей Куксенко — Stream API, часть 2

    @Test
    public void employersStuffLists() {
        List<Employee> employees = getEmployees();

        Map<String, List<Person>> employersStuffLists = employees.stream()
                .flatMap(emp -> {
                    Person per = emp.getPerson();
                    return emp.getJobHistory()
                            .stream()
                            .map(job -> new Job(per, job.getEmployer(), job.getDuration(), job.getPosition()));
                })
                .collect(groupingBy(Job::getEmployer,
                            mapping(Job::getPerson, toList())));

        assertEquals(Collections.singletonList(new Person("Bob", "White", 31)),
                employersStuffLists.get("uralvagonzavod"));
    }

    @Test
    public void indexByFirstEmployer() {
        Map<String, List<Person>> employeesIndex = null;// TODO
        throw new UnsupportedOperationException();
    }

    @Test
    public void greatestExperiencePerEmployer() {

        Map<String, Person> employeesIndex = getEmployees().stream()
                .flatMap(emp -> {
                    Person per = emp.getPerson();
                    return emp.getJobHistory()
                            .stream()
                            .map(job -> new Job(per, job.getEmployer(), job.getDuration(), job.getPosition()));
                })
                .collect(groupingBy(Job::getEmployer,
                        collectingAndThen(
                                maxBy(Comparator.comparing(el -> el.getDuration())), res -> res.get().getPerson())));

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
                        Arrays.asList(
                                new JobHistoryEntry(6, "QA", "epam"),
                                new JobHistoryEntry(1, "QA", "uralvagonzavod")
                        ))
        );
    }

}
