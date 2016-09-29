package part2.exercise;

import data.Employee;
import data.JobHistoryEntry;
import data.Person;
import org.junit.Test;

import java.util.*;
import java.util.function.*;
import java.util.stream.Collector;

import static java.util.stream.Collectors.*;

public class CollectorsExercise1 {

    @Test
    public void getTheCoolestOne() {
        final Map<String, Person> coolestByPosition = getCoolestByPosition(getEmployees());

        coolestByPosition.forEach((position, person) -> System.out.println(position + " -> " + person));
    }

    private static class PersonPositionDuration {
        private Person person = null;
        private String position = "";
        private int duration = 0;

        public PersonPositionDuration(Person person, String position, int duration) {
            this.person = person;
            this.position = position;
            this.duration = duration;
        }

        public void setPerson(Person person) {
            this.person = person;
        }

        public void setPosition(String position) {
            this.position = position;
        }

        public void setDuration(int duration) {
            this.duration = duration;
        }

        public PersonPositionDuration() {
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

        Map<String, Person> mapByPosition = employees.stream()
                .flatMap(employee -> employee.getJobHistory().stream()
                        .map(entry -> new PersonPositionDuration(employee.getPerson(), entry.getPosition(), entry.getDuration())))
                .collect(
                        groupingBy(
                                PersonPositionDuration::getPosition,
                                collectingAndThen(maxBy(Comparator.comparing(PersonPositionDuration::getDuration)), p -> p.get().getPerson())
                        ));

        employees.stream()
                .flatMap(employee -> employee.getJobHistory().stream()
                        .map(entry -> new PersonPositionDuration(employee.getPerson(), entry.getPosition(), entry.getDuration())))
                .collect(toMap(PersonPositionDuration::getPosition, Function.identity(), BinaryOperator.maxBy(Comparator.comparing(PersonPositionDuration::getDuration))))
                .entrySet().stream()
                .collect(toMap(Map.Entry::getKey, entry -> entry.getValue().getPerson()));


        // Second option
        // Collectors.toMap
        // iterate twice: stream...collect(...).stream()...
        // TODO написать свой коллектор

        Map<String, Person> withOwnCollector = employees.stream()
                .flatMap(employee -> employee.getJobHistory().stream()
                        .map(entry -> new PersonPositionDuration(employee.getPerson(), entry.getPosition(), entry.getDuration())))
                .collect(new CoolestByPositionCollector());
        throw new UnsupportedOperationException();
    }

    private static class CoolestByPositionCollector implements Collector<PersonPositionDuration, Map<String, PersonPositionDuration>, Map<String, Person>> {
        BiFunction resolver = BinaryOperator.maxBy(Comparator.comparing(PersonPositionDuration::getDuration));

        @Override
        public Supplier<Map<String, PersonPositionDuration>> supplier() {
            return HashMap::new;
        }

        @Override
        public BiConsumer<Map<String, PersonPositionDuration>, PersonPositionDuration> accumulator() {
            return (m, p) -> m.merge(p.getPosition(), p, resolver);
        }

        @Override
        public BinaryOperator<Map<String, PersonPositionDuration>> combiner() {
            return (left, right) -> {
                right.entrySet().forEach(entry -> left.merge(entry.getKey(), entry.getValue(), resolver));
                return left;
            };
        }

        @Override
        public Function<Map<String, PersonPositionDuration>, Map<String, Person>> finisher() {
            return (m1) -> m1.entrySet().stream()
                    .collect(
                            toMap(
                                    Map.Entry::getKey,
                                    entry -> entry.getValue().getPerson()
                            )
                    );
        }

        @Override
        public Set<Characteristics> characteristics() {
            return Collections.EMPTY_SET;
        }
    }

    @Test
    public void getTheCoolestOne2() {
        final Map<String, Person> coolestByPosition = getCoolestByPosition2(getEmployees());

        coolestByPosition.forEach((position, person) -> System.out.println(position + " -> " + person));
    }

    // With the longest sum duration on this position
    // { John Doe, [{dev, google, 4}, {dev, epam, 4}] } предпочтительнее, чем { A B, [{dev, google, 6}, {QA, epam, 100}]}
    private Map<String, Person> getCoolestByPosition2(List<Employee> employees) {

        Map<String, Person> map = employees.stream()
                .flatMap(employee -> employee.getJobHistory().stream()
                        .collect(groupingBy(JobHistoryEntry::getPosition, summingInt(JobHistoryEntry::getDuration)))
                        .entrySet().stream()
                        .map(entry -> new PersonPositionDuration(employee.getPerson(), entry.getKey(), entry.getValue())))
                .collect(
                        groupingBy(
                                PersonPositionDuration::getPosition,
                                collectingAndThen(
                                        maxBy(Comparator.comparing(PersonPositionDuration::getDuration)),
                                        p -> p.get().getPerson()
                                )
                        )
                );

        Map<String, Person> mapWithCollector = employees.stream()
                .flatMap(employee -> employee.getJobHistory().stream()
                        .collect(
                                new Collector<JobHistoryEntry, Map<String, Integer>, Map<String, Integer>>() {
                                    BiFunction<Integer, Integer, Integer> resolver = Integer::sum;

                                    @Override
                                    public Supplier<Map<String, Integer>> supplier() {
                                        return HashMap::new;
                                    }

                                    @Override
                                    public BiConsumer<Map<String, Integer>, JobHistoryEntry> accumulator() {
                                        return (map, e) -> map.merge(e.getPosition(), e.getDuration(), resolver);
                                    }

                                    @Override
                                    public BinaryOperator<Map<String, Integer>> combiner() {
                                        return (left, right) -> {
                                            right.entrySet().forEach(entry -> left.merge(entry.getKey(), entry.getValue(), resolver));
                                            return left;
                                        };
                                    }

                                    @Override
                                    public Function<Map<String, Integer>, Map<String, Integer>> finisher() {
                                        return Function.identity();
                                    }

                                    @Override
                                    public Set<Characteristics> characteristics() {
                                        return Collections.unmodifiableSet(EnumSet.of(Collector.Characteristics.IDENTITY_FINISH));
                                    }
                                }
                        )
                        .entrySet().stream()
                        .map(entry -> new PersonPositionDuration(employee.getPerson(), entry.getKey(), entry.getValue()))
                )
                .collect(new CoolestByPositionCollector());


        return mapWithCollector;
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
