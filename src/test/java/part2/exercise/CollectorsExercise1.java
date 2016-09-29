package part2.exercise;

import data.Employee;
import data.JobHistoryEntry;
import data.Person;
import org.junit.Test;

import java.util.*;
import java.util.function.BinaryOperator;
import java.util.function.Function;

import static java.util.stream.Collectors.*;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class CollectorsExercise1 {

    @Test
    public void getTheCoolestOne() {
        Map<String, Person> coolestByPosition = getCoolestByPosition(getEmployees());

        coolestByPosition.forEach((position, person) -> System.out.println(position + " -> " + person));

        final Person coolestDev = new Person("John", "Doe", 30);
        assertThat(coolestByPosition.get("dev"), is(coolestDev));
 //       coolestByPosition = getCoolestByPositionWithCustomCollector(getEmployees());
        assertThat(coolestByPosition.get("dev"), is(coolestDev));
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

        //noinspection OptionalGetWithoutIsPresent
        final Map<String, Person> coolestByPosition1 = employees.stream()
                .flatMap(e -> e.getJobHistory().stream()
                        .map(jhe -> new PersonPositionDuration(e.getPerson(), jhe.getPosition(), jhe.getDuration())))
                .collect(groupingBy(PersonPositionDuration::getPosition,
                        collectingAndThen(
                                maxBy(Comparator.comparing(PersonPositionDuration::getDuration)),
                                ppd -> ppd.get().getPerson())));

        final Map<String, Person> coolestByPosition2 = employees.stream()
                .flatMap(e -> e.getJobHistory().stream()
                        .map(jhe -> new PersonPositionDuration(e.getPerson(), jhe.getPosition(), jhe.getDuration())))
                .collect(toMap(PersonPositionDuration::getPosition,
                        Function.identity(),
                        BinaryOperator.maxBy(Comparator.comparing(PersonPositionDuration::getDuration))))
                .entrySet().stream().collect(toMap(Map.Entry::getKey,
                        entry -> entry.getValue().getPerson()));

        return coolestByPosition2;
    }

    private Map<String, Person> getCoolestByPositionWithCustomCollector(List<Employee> employees) {
        throw new UnsupportedOperationException(); // TODO: написать свой коллектор
        // With the longest duration on single job
    }


    @Test
    public void getTheCoolestOne2() {
        Map<String, Person> coolestByPosition = getCoolestByPosition2(getEmployees());

        coolestByPosition.forEach((position, person) -> System.out.println(position + " -> " + person));

        final Person coolestDev = new Person("John", "Galt", 23);
        assertThat(coolestByPosition.get("dev"), is(coolestDev));
//        coolestByPosition = getCoolestByPosition2WithCustomCollector(getEmployees());
        assertThat(coolestByPosition.get("dev"), is(coolestDev));
    }


    // With the longest sum duration on this position
    // { John Doe, [{dev, google, 4}, {dev, epam, 4}] } предпочтительнее, чем { A B, [{dev, google, 6}, {QA, epam, 100}]}
    private Map<String, Person> getCoolestByPosition2(List<Employee> employees) {
        //noinspection OptionalGetWithoutIsPresent
        return employees.stream()
                .flatMap(e -> e.getJobHistory().stream()
                        .collect(groupingBy(JobHistoryEntry::getPosition,
                                summingInt(JobHistoryEntry::getDuration)))
                        .entrySet().stream()
                        .map(posDurEntry -> new PersonPositionDuration(e.getPerson(), posDurEntry.getKey(), posDurEntry.getValue())))
                .collect(groupingBy(PersonPositionDuration::getPosition,
                        collectingAndThen(
                                maxBy(Comparator.comparing(PersonPositionDuration::getDuration)),
                                ppd -> ppd.get().getPerson())));
    }

    private Map<String, Person> getCoolestByPosition2WithCustomCollector(List<Employee> employees) {
        throw new UnsupportedOperationException(); // TODO: написать свой коллектор
        // With the longest sum duration on this position

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
                                new JobHistoryEntry(3, "dev", "google")
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

}
