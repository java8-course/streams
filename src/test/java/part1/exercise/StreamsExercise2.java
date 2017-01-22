package part1.exercise;

import data.Employee;
import data.JobHistoryEntry;
import data.Person;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.junit.Test;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class StreamsExercise2 {
    // https://youtu.be/kxgo7Y4cdA8 Сергей Куксенко и Алексей Шипилёв — Через тернии к лямбдам, часть 1
    // https://youtu.be/JRBWBJ6S4aU Сергей Куксенко и Алексей Шипилёв — Через тернии к лямбдам, часть 2

    // https://youtu.be/O8oN4KSZEXE Сергей Куксенко — Stream API, часть 1
    // https://youtu.be/i0Jr2l3jrDA Сергей Куксенко — Stream API, часть 2

    // TODO class PersonEmployerPair
    @Getter
    @AllArgsConstructor
    private static class PersonEmployerPair {
        private final Person person;
        private final String employer;
        private final int duration;

        PersonEmployerPair(Person person, String employer) {
            this.person = person;
            this.employer = employer;
            this.duration = 0;
        }
    }

    @Test
    public void employersStuffLists() {
        Map<String, List<Person>> employersStuffLists = getEmployees().stream()
                .flatMap(e -> e.getJobHistory().stream()
                        .map(j -> new PersonEmployerPair(e.getPerson(), j.getEmployer()))
                )
                .collect(Collectors.groupingBy(
                        PersonEmployerPair::getEmployer,
                        Collectors.mapping(PersonEmployerPair::getPerson, Collectors.toList())
                ));


        List<Person> googlePersons = new ArrayList<>();
        googlePersons.add(new Person("John", "Galt", 20));
        googlePersons.add(new Person("John", "Galt", 23));
        googlePersons.add(new Person("John", "Galt", 26));
        googlePersons.add(new Person("John", "Galt", 29));

        assertArrayEquals(employersStuffLists.get("google").toArray(), googlePersons.toArray());
    }

    @Test
    public void indexByFirstEmployer() {
        Map<String, List<Person>> employeesIndex = getEmployees().stream()
                .map(e -> new PersonEmployerPair(e.getPerson(), e.getJobHistory().get(0).getEmployer()))
                .collect(Collectors.groupingBy(
                        PersonEmployerPair::getEmployer,
                        Collectors.mapping(PersonEmployerPair::getPerson, Collectors.toList())
                ));

        List<Person> yandexPersons = new ArrayList<>();
        yandexPersons.add(new Person("John", "Doe", 21));
        yandexPersons.add(new Person("John", "Doe", 24));
        yandexPersons.add(new Person("Bob", "Doe", 27));
        yandexPersons.add(new Person("John", "Doe", 30));

        assertArrayEquals(employeesIndex.get("yandex").toArray(), yandexPersons.toArray());
        assertNull(employeesIndex.get("google"));
    }

    @Test
    public void greatestExperiencePerEmployer() {
        final Map<String, Person> employeesIndex = getEmployees().stream()
                .flatMap(e -> e.getJobHistory().stream()
                        .map( j -> new PersonEmployerPair(e.getPerson(), j.getEmployer(), j.getDuration()))
                )
                .collect(Collectors.groupingBy(
                        PersonEmployerPair::getEmployer,
                        Collectors.collectingAndThen(
                                Collectors.maxBy(Comparator.comparingInt(PersonEmployerPair::getDuration)),
                                p -> p.get().getPerson()
                        )
                ));

        /* First solution */
//        getEmployees().stream()
//                .flatMap(e -> e.getJobHistory().stream()
//                        .map(j -> new Employee(e.getPerson(), Collections.singletonList(j)))
//                )
//                .sorted(Comparator.comparingInt(e -> (-1)*e.getJobHistory().get(0).getDuration()))
//                .forEachOrdered(e -> {
//                    if (!employeesIndex.containsKey(e.getJobHistory().get(0).getEmployer())) {
//                        employeesIndex.put(e.getJobHistory().get(0).getEmployer(), e.getPerson());
//                    }
//                });

        assertEquals(new Person("John", "White", 28), employeesIndex.get("epam"));
        assertEquals(new Person("John", "Galt", 20), employeesIndex.get("google"));
        assertEquals(new Person("John", "Doe", 30), employeesIndex.get("abc"));
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
