package part2.exercise;

import data.Employee;
import data.JobHistoryEntry;
import data.Person;
import org.junit.Assert;
import org.junit.Test;
import part1.exercise.StreamsExercise2;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.*;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static java.util.stream.Collectors.*;

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
        // First option
        // Collectors.maxBy
        // Collectors.collectingAndThen
        // Collectors.groupingBy

        final Map<String, Person> collect = getEmployees()
                .stream()
                .flatMap(e -> getDurationsByPosition(e))
                .collect(Collectors.groupingBy(PersonPositionDuration::getPosition,
                        collectingAndThen(maxBy(Comparator.comparing(PersonPositionDuration::getDuration)),
                                p -> p.get().getPerson())));

        // Second option
        // Collectors.toMap
        // iterate twice: stream...collect(...).stream()...
        // TODO

        final Map<String, Person> collect2 = getEmployees()
                .stream()
                .flatMap(e -> getDurationsByPosition(e))
                .collect(Collectors.toMap(PersonPositionDuration::getPosition, Function.identity(),
                        BinaryOperator.maxBy(Comparator.comparing(PersonPositionDuration::getDuration))))
                .entrySet()
                .stream()
                .collect(Collectors.toMap(Map.Entry::getKey, entry -> entry.getValue().getPerson()));


        final Map<String, Person> collect3 = getEmployees()
                .stream()
                .flatMap(e -> getDurationsByPosition(e))
                .collect(new Collector<PersonPositionDuration, Map<String, PersonPositionDuration>, Map<String, Person>>() {

                    BiFunction biFunction = BinaryOperator.maxBy(Comparator.comparing(PersonPositionDuration::getDuration));

                    @Override
                    public Supplier<Map<String, PersonPositionDuration>> supplier() {
                        return HashMap::new;
                    }

                    @Override
                    public BiConsumer<Map<String, PersonPositionDuration>, PersonPositionDuration> accumulator() {
                        return (m, p) -> m.merge(p.getPosition(), p, biFunction);
                    }

                    @Override
                    public BinaryOperator<Map<String, PersonPositionDuration>> combiner() {
                        return (left, right) -> {
                            right.entrySet().forEach(entry -> left.merge(entry.getKey(), entry.getValue(), biFunction));
                            return left;
                        };
                    }

                    @Override
                    public Function<Map<String, PersonPositionDuration>, Map<String, Person>> finisher() {
                        return (m1) -> m1.entrySet().stream()
                                .collect(Collectors.toMap(Map.Entry::getKey, entry -> entry.getValue().getPerson()));
                    }

                    @Override
                    public Set<Characteristics> characteristics() {
                        return Collections.emptySet();
                    }
                });

        Assert.assertEquals(collect, collect2);
        Assert.assertEquals(collect2, collect3);

        return collect;
    }


    private Stream<PersonPositionDuration> getDurationsByPosition(Employee e) {
        Map<String, Integer> collect = e.getJobHistory().stream()
                .collect(Collectors.toMap(JobHistoryEntry::getPosition, JobHistoryEntry::getDuration, (d1, d2) -> (d1 + d2)));
        return collect.keySet()
                .stream()
                .map(s -> new PersonPositionDuration(e.getPerson(), s, collect.get(s)));
    }

    private List<PersonPositionDuration> getFullDurationByPosition(Employee e) {
        Map<String, Integer> collect = getDurationsByPosition(e).collect(Collectors.toMap(PersonPositionDuration::getPosition,
                PersonPositionDuration::getDuration, (d1, d2) -> d1 + d2));

        List<PersonPositionDuration> personPositionDurations = new ArrayList<>();
        for (String s: collect.keySet()) {
            personPositionDurations.add(new PersonPositionDuration(e.getPerson(), s, collect.get(s)));
        }
        return personPositionDurations;
    }


    @Test
    public void getTheCoolestOne2() {
        final Map<String, Person> coolestByPosition = getCoolestByPosition2(getEmployees());

        coolestByPosition.forEach((position, person) -> System.out.println(position + " -> " + person));
    }

    // With the longest sum duration on this position
    // { John Doe, [{dev, google, 4}, {dev, epam, 4}] } предпочтительнее, чем { A B, [{dev, google, 6}, {QA, epam, 100}]}
    private Map<String, Person> getCoolestByPosition2(List<Employee> employees) {
        return employees
                .stream()
                .flatMap(e -> getFullDurationByPosition(e)
                        .stream())
                .collect(groupingBy(PersonPositionDuration::getPosition,
                        collectingAndThen(maxBy(Comparator.comparing(PersonPositionDuration::getDuration)), e -> e.get().person)));
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

}
