package part1.exercise;

import data.Employee;
import data.JobHistoryEntry;
import data.Person;
import org.junit.Test;

import java.util.*;

import static java.util.stream.Collectors.*;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;

public class StreamsExercise2 {
    // https://youtu.be/kxgo7Y4cdA8 Сергей Куксенко и Алексей Шипилёв — Через тернии к лямбдам, часть 1
    // https://youtu.be/JRBWBJ6S4aU Сергей Куксенко и Алексей Шипилёв — Через тернии к лямбдам, часть 2

    // https://youtu.be/O8oN4KSZEXE Сергей Куксенко — Stream API, часть 1
    // https://youtu.be/i0Jr2l3jrDA Сергей Куксенко — Stream API, часть 2

    // TODO class PersonEmployerDuration
    private static class PersonEmployerDuration
    {
        private Person person;
        private String employer;
        private Integer duration;

        PersonEmployerDuration(Person p, String e) {
            this(p,e,0);
        }

        PersonEmployerDuration(Person p, String e, Integer d) {
            employer = e;
            person = p;
            duration = d;
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
                .flatMap(e -> e.getJobHistory()
                        .stream()
                        .map(j -> new PersonEmployerDuration(
                                e.getPerson(),
                                j.getEmployer())))
                .collect(groupingBy(
                        PersonEmployerDuration::getEmployer,
                        mapping(PersonEmployerDuration::getPerson, toList())));

        assertThat(employersStuffLists.get("Microsoft"),
                equalTo(Collections.singletonList(new Person("John", "White", 25))));
    }

    @Test
    public void indexByFirstEmployer() {
        Map<String, List<Person>> employeesIndex = getEmployees().stream()
                .map(e -> new PersonEmployerDuration(
                        e.getPerson(),
                        e.getJobHistory().get(0).getEmployer()))
                .collect(groupingBy(PersonEmployerDuration::getEmployer,
                        mapping(PersonEmployerDuration::getPerson, toList())));

        assertThat(employeesIndex.get("abc"), equalTo(null));
        assertThat(employeesIndex.get("yandex"), equalTo(Arrays.asList(
                new Person("John", "Doe", 21),
                new Person("John", "Doe", 24),
                new Person("Bob", "Doe", 27),
                new Person("John", "Doe", 30))));
    }

    private static Map<String, Integer> jobHistory(List<JobHistoryEntry> jobHistory){
        return jobHistory.stream()
                .collect(groupingBy(
                        JobHistoryEntry::getEmployer,
                        summingInt(JobHistoryEntry::getDuration)));
    }

    @Test
    public void greatestExperiencePerEmployer() {
        Map<String, Person> employeesIndex = getEmployees().stream()
                .flatMap(e -> jobHistory(e.getJobHistory())
                        .entrySet().stream()
                        .map(j -> new PersonEmployerDuration(
                                e.getPerson(),
                                j.getKey(),
                                j.getValue()))
                )
                .collect(groupingBy(
                        PersonEmployerDuration::getEmployer,
                        collectingAndThen(maxBy(Comparator.comparingInt(PersonEmployerDuration::getDuration)),
                                p->p.get().getPerson()))
                );


        assertThat(new Person("John", "White", 28), equalTo(employeesIndex.get("epam")));
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
                                new JobHistoryEntry(6, "QA", "Microsoft")
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
