package part1.exercise;

import data.Employee;
import data.JobHistoryEntry;
import data.Person;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.junit.Test;

import java.util.*;

import static java.util.stream.Collectors.*;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

public class StreamsExercise2 {
    // https://youtu.be/kxgo7Y4cdA8 Сергей Куксенко и Алексей Шипилёв — Через тернии к лямбдам, часть 1
    // https://youtu.be/JRBWBJ6S4aU Сергей Куксенко и Алексей Шипилёв — Через тернии к лямбдам, часть 2

    // https://youtu.be/O8oN4KSZEXE Сергей Куксенко — Stream API, часть 1
    // https://youtu.be/i0Jr2l3jrDA Сергей Куксенко — Stream API, часть 2

    // TODO class PersonEmployerPair

    @Data
    @AllArgsConstructor
    private static class Pair<F, S> {
        private F first;
        private S second;
    }

    @Test
    public void employersStuffLists() {
        Map<String, List<Person>> expected = new HashMap<>();
        for (Employee e : getEmployees()){
            e.getJobHistory().forEach(j -> {
                final String employer = j.getEmployer();
                if (expected.containsKey(employer))
                    expected.get(employer).add(e.getPerson());
                else {
                    List<Person> list = new ArrayList<>();
                    list.add(e.getPerson());
                    expected.put(employer, list);
                }
            });
        }

        // TODO
        Map<String, List<Person>> actual = getEmployees().stream()
                .flatMap(e -> e.getJobHistory().stream()
                                    .map(j -> new Pair<>(j.getEmployer(), e.getPerson())))
                .collect(groupingBy(
                        Pair::getFirst,
                        mapping(Pair::getSecond, toList())
                ));
        assertThat(actual, is(expected));
    }

    @Test
    public void indexByFirstEmployer() {
        Map<String, List<Person>> expected = new HashMap<>();
        for (Employee e : getEmployees()){
            if (e.getJobHistory().size() > 0) {
                final String firstEmployer = e.getJobHistory().get(0).getEmployer();
                if (expected.containsKey(firstEmployer))
                    expected.get(firstEmployer).add(e.getPerson());
                else {
                    List<Person> list = new ArrayList<>();
                    list.add(e.getPerson());
                    expected.put(firstEmployer, list);
                }
            }
        }

        // TODO
        Map<String, List<Person>> actual = getEmployees().stream()
                .flatMap(e -> e.getJobHistory().stream().limit(1)
                        .map(j -> new Pair<>(j.getEmployer(), e.getPerson())))
                .collect(groupingBy(
                        Pair::getFirst,
                        mapping(Pair::getSecond, toList())
                ));
        assertThat(actual, is(expected));
    }

    @Test
    public void greatestExperiencePerEmployer() {
        // TODO
        Map<String, Person> employeesIndex = getEmployees().stream()
                .flatMap(e -> e.getJobHistory().stream().limit(1)
                        .map(j -> new Pair<>(new Pair<>(j.getEmployer(), e.getPerson()), j.getDuration())))
                .collect(
                        groupingBy(
                            t -> t.getFirst().getFirst(),
                            collectingAndThen(
                                    maxBy(Comparator.comparingInt(Pair::getSecond)),
                                    p -> p.get().getFirst().getSecond()
                            )
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
