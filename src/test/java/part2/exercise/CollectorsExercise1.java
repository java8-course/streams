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

public class CollectorsExercise1 {

    @Test
    public void getTheCoolestOne() {
        final Map<String, Person> coolestByPosition = getCoolestByPosition(getEmployees());

        coolestByPosition.forEach((position, person) -> System.out.println(position + " -> " + person));
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
    private Map<String, Person> getCoolestByPosition(List<Employee> employees) {
        /*employees.stream().flatMap(employee -> {
            Stream<JobHistoryEntry> stream = employee.getJobHistory().stream();
            return stream.flatMap(jobHistoryEntry -> {
                PersonPositionDuration personPositionDuration = new PersonPositionDuration(employee.getPerson(), jobHistoryEntry.getPosition(), jobHistoryEntry.getDuration());
                return Stream.of(personPositionDuration);
            });
        });*/

/*
        final Optional<PersonPositionDuration> optional = employees.stream().flatMap(employee ->
                employee.getJobHistory().stream()
                        .map(p -> new PersonPositionDuration(employee.getPerson(), p.getPosition(), p.getDuration()))
        ).collect(Collectors.maxBy((o1, o2) -> Integer.compare(o1.duration, o2.duration)));
*/

        final Map<String, Person> collect = employees.stream().flatMap(e -> e.getJobHistory().stream()
                .map(j -> new PersonPositionDuration(e.getPerson(), j.getPosition(), j.getDuration())))
                .collect(Collectors.toMap(PersonPositionDuration::getPosition,
                        Function.identity(),
                        BinaryOperator.maxBy(Comparator.comparingInt(o -> o.duration)))
                ).entrySet()
                .stream()
                .collect(Collectors.toMap(Map.Entry::getKey, p -> p.getValue().getPerson()));



        // First option
        // Collectors.maxBy
        // Collectors.collectingAndThen
        // Collectors.groupingBy

        // Second option
        // Collectors.toMap
        // iterate twice: stream...collect(...).stream()...

        return collect;
    }

    private Map<String, Person> getCoolestByPositionWay2(List<Employee> employees) {
        /*employees.stream().flatMap(employee -> {
            Stream<JobHistoryEntry> stream = employee.getJobHistory().stream();
            return stream.flatMap(jobHistoryEntry -> {
                PersonPositionDuration personPositionDuration = new PersonPositionDuration(employee.getPerson(), jobHistoryEntry.getPosition(), jobHistoryEntry.getDuration());
                return Stream.of(personPositionDuration);
            });
        });*/

/*
        final Optional<PersonPositionDuration> optional = employees.stream().flatMap(employee ->
                employee.getJobHistory().stream()
                        .map(p -> new PersonPositionDuration(employee.getPerson(), p.getPosition(), p.getDuration()))
        ).collect(Collectors.maxBy((o1, o2) -> Integer.compare(o1.duration, o2.duration)));
*/

        final Map<String, Person> collect = employees.stream().flatMap(e -> e.getJobHistory().stream()
                .map(j -> new PersonPositionDuration(e.getPerson(), j.getPosition(), j.getDuration())))
                .



        // First option
        // Collectors.maxBy
        // Collectors.collectingAndThen
        // Collectors.groupingBy

        // Second option
        // Collectors.toMap
        // iterate twice: stream...collect(...).stream()...

        return collect;
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
        throw new UnsupportedOperationException();
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

    private class JobEntry {
        private Person person;
        private String postiton;
        private int duration;
        private String employer;

        public JobEntry(Person person, String postiton, int duration, String employer) {
            this.person = person;
            this.postiton = postiton;
            this.duration = duration;
            this.employer = employer;
        }

        public Person getPerson() {
            return person;
        }

        public String getPostiton() {
            return postiton;
        }

        public int getDuration() {
            return duration;
        }

        public String getEmployer() {
            return employer;
        }
    }

}
