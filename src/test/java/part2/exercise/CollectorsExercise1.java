package part2.exercise;

import data.Employee;
import data.JobHistoryEntry;
import data.Person;
import org.junit.Test;

import java.util.*;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.*;
import static org.junit.Assert.assertEquals;

public class CollectorsExercise1 {

    @Test
    public void getTheCoolestOne() {
        final Map<String, Person> coolestByPositionFirst = getCoolestByPositionFirst(getEmployees());
        final Map<String, Person> coolestByPositionSecond = getCoolestByPositionSecond(getEmployees());

        assertEquals(coolestByPositionFirst, coolestByPositionSecond);
    }

    private static class PersonPositionDuration {
        private final Person person;
        private final String position;
        private final int duration;

        public PersonPositionDuration(Person person, String position, int duration) {
            this.person = person;
            this.position = position;
            this.duration = duration;
        }

        public Person getPerson() {
            return person;
        }

        public String getPosition() {
            return position;
        }

        public int getDuration() {
            return duration;
        }
    }

    // With the longest duration on single job
    private Map<String, Person> getCoolestByPositionFirst(List<Employee> employees) {
        // First option
        // Collectors.maxBy
        // Collectors.collectingAndThen
        // Collectors.groupingBy
        // TODO
        return employees.stream()
                .flatMap(e -> e.getJobHistory().stream()
                        .map(j -> new PersonPositionDuration(e.getPerson(), j.getPosition(), j.getDuration())))
                .collect(
                        groupingBy(
                                PersonPositionDuration::getPosition,
                                collectingAndThen(
                                        maxBy(Comparator.comparingInt(PersonPositionDuration::getDuration)),
                                        o -> o.orElseThrow(IllegalStateException::new).getPerson()
                                )
                        )
                );
    }

    private Map<String, Person> getCoolestByPositionSecond(List<Employee> employees) {
        // Second option
        // Collectors.toMap
        // iterate twice: stream...collect(...).stream()...
        // TODO
        return employees.stream()
                .flatMap(e -> e.getJobHistory().stream()
                        .map(j -> new PersonPositionDuration(e.getPerson(), j.getPosition(), j.getDuration())))
                .collect(toMap(
                                PersonPositionDuration::getPosition,
                                o -> new PersonPositionDuration(o.getPerson(), null, o.getDuration()),
                                (p1, p2) -> p1.getDuration() > p2.getDuration() ? p1 : p2
                )).entrySet().stream()
                .collect(toMap(Map.Entry::getKey, o -> o.getValue().getPerson()));
    }

    @Test
    public void getTheCoolestOne2() {
        final Map<String, Person> coolestByPosition = getCoolestByPosition2(getEmployees());

        coolestByPosition.forEach((position, person) -> System.out.println(position + " -> " + person));
    }

    // With the longest sum duration on this position
    // { John Doe, [{dev, google, 4}, {dev, epam, 4}] } предпочтительнее, чем { A B, [{dev, google, 6}, {QA, epam, 100}]}
    private Map<String, Person> getCoolestByPosition2(List<Employee> employees) {
        // TODO
        return employees.stream()
                .flatMap(e -> e.getJobHistory().stream()
                        .map(j -> new PersonPositionDuration(e.getPerson(), j.getPosition(), j.getDuration())))
                .collect(
                        groupingBy(
                                PersonPositionDuration::getPosition,
                                collectingAndThen(
                                        groupingBy(
                                                PersonPositionDuration::getPerson,
                                                summingInt(PersonPositionDuration::getDuration)
                                        ),
                                        m -> {
                                            Map.Entry<Person, Integer> maxEntry = null;
                                            for (Map.Entry<Person, Integer> entry : m.entrySet())
                                                if (maxEntry == null || entry.getValue().compareTo(maxEntry.getValue()) > 0)
                                                    maxEntry = entry;
                                            return maxEntry.getKey();
                                        }
                                )
                        )
                );
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
                                new JobHistoryEntry(6, "BA", "epam")
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
                                new JobHistoryEntry(10, "QA", "epam")
                        ))
        );
    }

}
