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

import static java.util.stream.Collectors.*;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;

public class CollectorsExercise1 {

    @Test
    public void getTheCoolestOne() {

        Person expected = new Person("John", "Doe", 30);

        final Map<String, Person> coolestByPosition0 = getCoolestByPosition0(getEmployees());

        coolestByPosition0.forEach((position, person) -> System.out.println(position + " -> " + person));
        assertThat(coolestByPosition0.get("dev"), equalTo(expected));
        System.out.println();

        final Map<String, Person> coolestByPosition1 = getCoolestByPosition1(getEmployees());

        coolestByPosition0.forEach((position, person) -> System.out.println(position + " -> " + person));
        assertThat(coolestByPosition1.get("dev"), equalTo(expected));
        System.out.println();

        final Map<String, Person> coolestByPositionCollector = getCoolestByPositionCollector(getEmployees());

        coolestByPosition0.forEach((position, person) -> System.out.println(position + " -> " + person));
        assertThat(coolestByPositionCollector.get("dev"), equalTo(expected));
        System.out.println();
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


    private Map<String, Person> getCoolestByPosition0(List<Employee> employees) {
        return employees.stream()
                .flatMap(this::getPersonPositionDuration)
                .collect(groupingBy(
                        PersonPositionDuration::getPosition,
                        collectingAndThen(
                                maxBy(Comparator.comparing(PersonPositionDuration::getDuration)),
                                pep -> pep.get().getPerson()
                        )
                ));
    }

    private Map<String, Person> getCoolestByPosition1(List<Employee> employees) {
        return employees.stream()
                .flatMap(this::getPersonPositionDuration)
                .collect(toMap(
                        PersonPositionDuration::getPosition,
                        Function.identity(),
                        (ppd1, ppd2) -> ppd1.getDuration() > ppd2.getDuration() ? ppd1 : ppd2))
                .entrySet().stream()
                .collect(toMap(
                        Map.Entry::getKey,
                        e -> e.getValue().getPerson()
                ));
    }

    private Map<String, Person> getCoolestByPositionCollector(List<Employee> employees) {
        return employees.stream()
                .flatMap(this::getPersonPositionDuration)
                .collect(collector);
    }

    private Collector<PersonPositionDuration, Map<String, PersonPositionDuration>, Map<String, Person>> collector =
            new Collector<PersonPositionDuration, Map<String, PersonPositionDuration>, Map<String, Person>>() {
                @Override
                public Supplier<Map<String, PersonPositionDuration>> supplier() {
                    return HashMap::new;
                }

                @Override
                public BiConsumer<Map<String, PersonPositionDuration>, PersonPositionDuration> accumulator() {
                    return (c, p) -> c.put(p.getPosition(), resolve(c, p));
                }

                private PersonPositionDuration resolve(Map<String, PersonPositionDuration> c, PersonPositionDuration p2) {
                    return Optional.ofNullable(c.get(p2.getPosition()))
                            .filter(p1 -> p1.getDuration() >= p2.getDuration())
                            .orElse(p2);
                }

                @Override
                public BinaryOperator<Map<String, PersonPositionDuration>> combiner() {
                    return this::combine;
                }

                private Map<String, PersonPositionDuration> combine(Map<String, PersonPositionDuration> m1,
                                                                    Map<String, PersonPositionDuration> m2) {
                    m1.putAll(m2.entrySet().stream()
                            .filter(e ->
                                    !m1.containsKey(e.getKey())
                                    || m1.get(e.getKey()).getDuration() < e.getValue().getDuration()
                            ).collect(toMap(Map.Entry::getKey, Map.Entry::getValue)));
                    return m1;
                }

                @Override
                public Function<Map<String, PersonPositionDuration>, Map<String, Person>> finisher() {
                    return m -> m.entrySet().stream()
                            .collect(toMap(Map.Entry::getKey, e -> e.getValue().getPerson()));
                }

                @Override
                public Set<Characteristics> characteristics() {
                    return Collections.emptySet();
                }
            };



    private Stream<PersonPositionDuration> getPersonPositionDuration(Employee employee) {
        return employee.getJobHistory().stream()
                .map(j -> new PersonPositionDuration(employee.getPerson(), j.getPosition(), j.getDuration()));
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

}
