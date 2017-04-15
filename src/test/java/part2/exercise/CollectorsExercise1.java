package part2.exercise;

import data.Employee;
import data.JobHistoryEntry;
import data.Person;
import org.junit.Test;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;
import java.util.stream.Stream;

import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.*;
import static org.junit.Assert.assertEquals;

public class CollectorsExercise1 {

    @Test
    public void getTheCoolestOne() {
        final Map<String, Person> coolestByPosition1 = getCoolestByPositionVar1(getEmployees());
        final Map<String, Person> coolestByPosition2 = getCoolestByPositionVar2(getEmployees());

        assertEquals(coolestByPosition1, coolestByPosition2);
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

    private Stream<PersonPositionDuration> getPersonPositionDurationStream(List<Employee> employees) {
        return employees
                .stream()
                .flatMap(
                        e -> e.getJobHistory().stream()
                                .map(j -> new PersonPositionDuration(e.getPerson(), j.getPosition(), j.getDuration()))
                );
    }

    private Map<String, Person> getCoolestByPositionVar1(List<Employee> employees) {
        return getPersonPositionDurationStream(employees)
                .collect(
                        groupingBy(
                                PersonPositionDuration::getPosition,
                                collectingAndThen(maxBy(comparing(PersonPositionDuration::getDuration)), ppd -> ppd.get().getPerson())
                        )
                );
    }

    private Map<String, Person> getCoolestByPositionVar2(List<Employee> employees) {
        return getPersonPositionDurationStream(employees)
                .collect(
                        toMap(
                                PersonPositionDuration::getPosition,
                                Function.identity(),
                                BinaryOperator.maxBy(comparing(PersonPositionDuration::getDuration))
                        )
                )
                .entrySet()
                .stream()
                .collect(
                        toMap(
                                Map.Entry::getKey,
                                ppd -> ppd.getValue().getPerson()
                        )
                );
    }

    @Test
    public void getTheCoolestOne2() {
        final Map<String, Person> coolestByPosition1 = getCoolestByPositionVar1(getEmployees());
        final Map<String, Person> coolestByPosition3 = getCoolestByPositionVar3(getEmployees());

        assertEquals(coolestByPosition1, coolestByPosition3);
    }

    private Map<String, Person> getCoolestByPositionVar3(List<Employee> employees) {
        return getPersonPositionDurationStream(employees).collect(
                new Collector<PersonPositionDuration, Map<String, PersonPositionDuration>, Map<String, Person>>() {
                    @Override
                    public Supplier<Map<String, PersonPositionDuration>> supplier() {
                        return HashMap::new;
                    }

                    @Override
                    public BiConsumer<Map<String, PersonPositionDuration>, PersonPositionDuration> accumulator() {
                        return (map, ppd) -> {
                            map.putIfAbsent(ppd.getPosition(), ppd);
                            PersonPositionDuration currentPpd = map.get(ppd.getPosition());
                            if (ppd.getDuration() > currentPpd.getDuration()) {
                                map.put(ppd.getPosition(), ppd);
                            }
                        };
                    }

                    @Override
                    public BinaryOperator<Map<String, PersonPositionDuration>> combiner() {
                        return (m1, m2) -> {
                            m2.forEach((k, v) -> m1.merge(k, v, (v1, v2) -> v1.getDuration() > v2.getDuration() ? v1 : v2));
                            return m1;
                        };
                    }

                    @Override
                    public Function<Map<String, PersonPositionDuration>, Map<String, Person>> finisher() {
                        return map -> map.entrySet()
                                .stream()
                                .collect(
                                        toMap(
                                                Map.Entry::getKey,
                                                item -> item.getValue().getPerson()
                                        )
                                );
                    }

                    @Override
                    public Set<Characteristics> characteristics() {
                        return Collections.unmodifiableSet(EnumSet.of(Characteristics.UNORDERED));
                    }
                }
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
                                new JobHistoryEntry(6, "QA", "epam")
                        ))
        );
    }

}