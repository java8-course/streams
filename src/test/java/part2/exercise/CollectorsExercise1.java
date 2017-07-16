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
import static org.junit.Assert.assertEquals;

public class CollectorsExercise1 {

    @Test
    public void getTheCoolestOne() {
        final Map<String, Person> coolestByPosition1 = getCoolestByPositionFirstOption(getEmployees());
        final Map<String, Person> coolestByPosition2 = getCoolestByPositionSecondOption(getEmployees());
        final Map<String, Person> coolestByPosition3 = getCoolestByPositionThirdOption(getEmployees());

        Person qa1 = coolestByPosition1.get("QA");
        Person qa2 = coolestByPosition2.get("QA");
        Person qa3 = coolestByPosition3.get("QA");

        assertEquals(qa1, qa2);
        assertEquals(qa2, qa3);
        assertEquals(qa1, qa3);

        Person dev1 = coolestByPosition1.get("dev");
        Person dev2 = coolestByPosition2.get("dev");
        Person dev3 = coolestByPosition3.get("dev");

        assertEquals(dev1, dev2);
        assertEquals(dev2, dev3);
        assertEquals(dev1, dev3);

        Person ba1 = coolestByPosition1.get("BA");
        Person ba2 = coolestByPosition2.get("BA");
        Person ba3 = coolestByPosition3.get("BA");

        assertEquals(ba1, ba2);
        assertEquals(ba2, ba3);
        assertEquals(ba1, ba3);

//        coolestByPosition3.forEach((position, person) -> System.out.println(position + " -> " + person));
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
    private Map<String, Person> getCoolestByPositionFirstOption(List<Employee> employees) {
        final Stream<PersonPositionDuration> personPositionDurationStream =
                getPersonPositionDurationStream(employees);

        // First option
        // Collectors.maxBy
        // Collectors.collectingAndThen
        // Collectors.groupingBy
        return personPositionDurationStream
                .collect(
                        groupingBy(
                                PersonPositionDuration::getPosition,
                                collectingAndThen(
                                        maxBy(Comparator.comparingInt(PersonPositionDuration::getDuration)),
                                        personPositionDuration -> personPositionDuration.get().getPerson()
                                )
                        )
                );
    }

    private Map<String, Person> getCoolestByPositionSecondOption(List<Employee> employees) {
        final Stream<PersonPositionDuration> personPositionDurationStream =
                getPersonPositionDurationStream(employees);

        // Second option
        // Collectors.toMap
        // iterate twice: stream...collect(...).stream()...

        final Map<String, PersonPositionDuration> intermediate =
                personPositionDurationStream
                        .collect(
                                toMap(
                                        PersonPositionDuration::getPosition,
                                        Function.identity(),
                                        (o1, o2) -> o1.getDuration() > o2.getDuration() ? o1 : o2
                                )
                        );

        return intermediate.entrySet()
                .stream()
                .collect(
                        toMap(
                                Map.Entry::getKey,
                                t -> t.getValue().getPerson())
                );
    }

    private Map<String, Person> getCoolestByPositionThirdOption(List<Employee> employees) {
        final Stream<PersonPositionDuration> personPositionDurationStream =
                getPersonPositionDurationStream(employees);

        // Third option
        // new Collector ...
        return personPositionDurationStream
                .collect(new Collector<PersonPositionDuration,
                                 Map<String, PersonPositionDuration>,
                                 Map<String, Person>>() {

                             @Override
                             public Supplier<Map<String, PersonPositionDuration>> supplier() {
                                 return HashMap::new;
                             }

                             @Override
                             public BiConsumer<Map<String, PersonPositionDuration>, PersonPositionDuration> accumulator() {
                                 return (map, ppd) -> {
                                     map.putIfAbsent(ppd.getPosition(), ppd);
                                     PersonPositionDuration current = map.get(ppd.getPosition());
                                     if (ppd.getDuration() > current.getDuration()) {
                                         map.put(ppd.getPosition(), ppd);
                                     }
                                 };
                             }

                             @Override
                             public BinaryOperator<Map<String, PersonPositionDuration>> combiner() {
                                 return (accumFirst, accumSecond) -> {
                                     accumSecond.forEach(
                                             (position, ppd) ->
                                                     accumFirst.merge(
                                                             position, ppd,
                                                             (ppd1, ppd2) ->
                                                                     ppd1.getDuration() > ppd2.getDuration() ? ppd1 : ppd2
                                                     )
                                     );
                                     return accumFirst;
                                 };
                             }

                             @Override
                             public Function<Map<String, PersonPositionDuration>, Map<String, Person>> finisher() {
                                 return map ->
                                         map.entrySet().stream()
                                                 .collect(
                                                         toMap(
                                                                 o -> o.getKey(),
                                                                 t -> t.getValue().getPerson()
                                                         )
                                                 );
                             }

                             @Override
                             public Set<Characteristics> characteristics() {
                                 return Collections.emptySet();
                             }
                         }
                );
    }


    @Test
    public void getTheCoolestOne2() {
        final Map<String, Person> coolestByPosition = getCoolestByPosition2(getEmployees());

        final Person qa = coolestByPosition.get("QA");
        assertEquals(new Person("Bob", "Doe", 27), qa);

        final Person dev = coolestByPosition.get("dev");
        assertEquals(new Person("John", "Galt", 26), dev);

        final Person ba = coolestByPosition.get("BA");
        assertEquals(new Person("John", "White", 28), ba);

//        coolestByPosition.forEach((position, person) -> System.out.println(position + " -> " + person));
    }

    // With the longest sum duration on this position
    // { John Doe, [{dev, google, 4}, {dev, epam, 4}] } предпочтительнее, чем { A B, [{dev, google, 6}, {QA, epam, 100}]}
    private Map<String, Person> getCoolestByPosition2(List<Employee> employees) {
        return getPersonPositionDurationStream(employees)
                .collect(
                        groupingBy(PersonPositionDuration::getPosition,
                                collectingAndThen(
                                        groupingBy(
                                                PersonPositionDuration::getPerson,
                                                summingInt(PersonPositionDuration::getDuration)
                                        ),
                                        m -> m.entrySet().stream()
                                                .sorted((o1, o2) -> Integer.compare(o2.getValue(), o1.getValue()))
                                                .map(Map.Entry::getKey)
                                                .findFirst()
                                                .get()
                                )
                        )
                );
    }


    private Stream<PersonPositionDuration> getPersonPositionDurationStream(List<Employee> employees) {
        return employees.stream()
                .flatMap(
                        e -> e.getJobHistory().stream()
                                .map(j ->
                                        new PersonPositionDuration(
                                                e.getPerson(),
                                                j.getPosition(),
                                                j.getDuration()
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
                                new JobHistoryEntry(7, "QA", "epam")
                        )),
                new Employee(
                        new Person("John", "Galt", 26),
                        Arrays.asList(
                                new JobHistoryEntry(4, "dev", "epam"),
                                new JobHistoryEntry(5, "dev", "google")
                        )),
                new Employee(
                        new Person("Bob", "Doe", 27),
                        Arrays.asList(
                                new JobHistoryEntry(4, "QA", "yandex"),
                                new JobHistoryEntry(4, "QA", "epam"),
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
