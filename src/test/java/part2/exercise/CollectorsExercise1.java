package part2.exercise;

import data.Employee;
import data.JobHistoryEntry;
import data.Person;
import org.junit.Test;

import java.util.*;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.*;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

public class CollectorsExercise1 {

    @Test
    public void getTheCoolestOne() {
        final Map<String, Person> coolestByPosition = getCoolestByPosition(getEmployees());

        Person expected = new Person("John", "Doe", 30);
        coolestByPosition.forEach((position, person) -> System.out.println(position + " -> " + person));
        assertEquals(coolestByPosition.get("dev"), expected);
    }

    // With the longest duration on single job
    private Map<String, Person> getCoolestByPosition(List<Employee> employees) {
        return employees.stream()
                .flatMap(e -> e.getJobHistory().stream()
                        .map(j -> new PersonPositionDuration(
                                e.getPerson(),
                                j.getPosition(),
                                j.getDuration())))
                .collect(toMap(PersonPositionDuration::getPosition,
                        Function.identity(),
                        BinaryOperator.maxBy(Comparator.comparingInt(o -> o.duration))))
                .entrySet().stream()
                .collect(toMap(Map.Entry::getKey, p -> p.getValue().getPerson()));
    }

    @Test
    public void getTheCoolestOneWay2() {
        final Map<String, Person> coolestByPosition = getCoolestByPositionWay2(getEmployees());

        Person expected = new Person("John", "Doe", 30);
        coolestByPosition.forEach((position, person) -> System.out.println(position + " -> " + person));
        assertEquals(coolestByPosition.get("dev"), expected);
    }

    private Map<String, Person> getCoolestByPositionWay2(List<Employee> employees) {
        return employees.stream()
                .flatMap(
                        e -> e.getJobHistory()
                                .stream()
                                .map(j -> new PersonPositionDuration(
                                        e.getPerson(),
                                        j.getPosition(),
                                        j.getDuration())))
                .collect(groupingBy(PersonPositionDuration::getPosition,
                        collectingAndThen(maxBy(Comparator.comparing(PersonPositionDuration::getDuration)),
                                p -> p.map(PersonPositionDuration::getPerson).get())
                ));
    }

    @Test
    public void getTheCoolestOne2() {
        final Map<String, Person> coolestByPosition = getCoolestByPosition2(getEmployees());

        final Person expectedDev = new Person("John", "Galt", 20);
        final Person expectedQA = new Person("John", "White", 22);
        final Person expectedBA = new Person("John", "White", 28);

        assertThat(coolestByPosition.get("dev"), is(expectedDev));
        assertThat(coolestByPosition.get("QA"), is(expectedQA));
        assertThat(coolestByPosition.get("BA"), is(expectedBA));

        coolestByPosition.forEach((position, person) -> System.out.println(position + " -> " + person));
    }

    // With the longest sum duration on this position
    // { John Doe, [{dev, google, 4}, {dev, epam, 4}] } предпочтительнее, чем { A B, [{dev, google, 6}, {QA, epam, 100}]}
    private Map<String, Person> getCoolestByPosition2(List<Employee> employees) {
        return employees.stream()
                .flatMap(e -> collectPositionDurations(e.getJobHistory())
                        .entrySet().stream()
                        .map(p -> new PersonPositionDuration(
                                e.getPerson(),
                                p.getKey(),
                                p.getValue()))
                ).collect(groupingBy(PersonPositionDuration::getPosition,
                        collectingAndThen(maxBy(Comparator.comparing(PersonPositionDuration::getDuration)),
                                p -> p.map(PersonPositionDuration::getPerson).get())
                ));
    }

    private Map<String, Integer> collectPositionDurations(List<JobHistoryEntry> list) {
        return list.stream()
                .collect(groupingBy(JobHistoryEntry::getPosition, summingInt(JobHistoryEntry::getDuration)));
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
                                new JobHistoryEntry(6, "QA", "epam")
                        ))
        );
    }

    private static class PersonPositionDuration {
        private final Person person;
        private final String position;
        private final int duration;

        PersonPositionDuration(Person person, String position, int duration) {
            this.person = person;
            this.position = position;
            this.duration = duration;
        }

        Person getPerson() {
            return person;
        }

        String getPosition() {
            return position;
        }

        int getDuration() {
            return duration;
        }
    }

}
