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
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.collectingAndThen;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.maxBy;
import static java.util.stream.Collectors.summingInt;
import static java.util.stream.Collectors.toMap;
import static org.junit.Assert.assertEquals;

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

        final Stream<PersonPositionDuration> persons = employees.stream()
            .flatMap(e -> e.getJobHistory().stream()
                .map(j -> new PersonPositionDuration(e.getPerson(), j.getPosition(), j.getDuration())));

        Map<String, Person> option1 = persons
            .collect(Collectors.groupingBy(
                PersonPositionDuration::getPosition,
                collectingAndThen(
                    maxBy(comparing(PersonPositionDuration::getDuration)),
                    p -> p.get().getPerson())
            ));

        // Second option
        // Collectors.toMap
        // iterate twice: stream...collect(...).stream()...

        Map<String, PersonPositionDuration> positionDurationMap = persons
            .collect(toMap(
                PersonPositionDuration::getPosition,
                Function.identity(),
                BinaryOperator.maxBy(comparing(PersonPositionDuration::getDuration))
            ));

        Map<String, Person> option2 = positionDurationMap.entrySet().stream()
            .collect(toMap(Map.Entry::getKey,
                           employee -> employee.getValue().getPerson()));

        //third option
        Map<String, Person> option3 = persons
            .collect(new Collector<PersonPositionDuration, Map<String, PersonPositionDuration>, Map<String, Person>>() {
                @Override
                public Supplier<Map<String, PersonPositionDuration>> supplier() {
                    return HashMap::new;
                }

                @Override
                public BiConsumer<Map<String, PersonPositionDuration>, PersonPositionDuration> accumulator() {
                    return (map, person) -> map.merge(
                        person.getPosition(),
                        person,
                        (ppd1, ppd2) -> ppd1.getDuration() > ppd2.getDuration() ? ppd1 : ppd2);
                }

                @Override
                public BinaryOperator<Map<String, PersonPositionDuration>> combiner() {
                    return (map1, map2) -> {
                        map2.forEach((key, value) -> map1.merge(key, value, (ppd1, ppd2) ->
                            ppd1.getDuration() > ppd2.getDuration() ? ppd1 : ppd2));
                        return map1;
                    };
                }

                @Override
                public Function<Map<String, PersonPositionDuration>, Map<String, Person>> finisher() {
                    return (map) -> map.entrySet().stream()
                        .collect(toMap(Map.Entry::getKey,
                                       employee -> employee.getValue().getPerson()));
                }

                @Override
                public Set<Characteristics> characteristics() {
                    return Collections.emptySet();
                }
            });

        return option1;
    }

    @Test
    public void getTheCoolestOne2() {
        final Map<String, Person> coolestByPosition = getCoolestByPosition2(getEmployees());

        assertEquals(new Person("John", "Doe", 30), coolestByPosition.get("QA"));
        assertEquals(new Person("John", "Galt", 26), coolestByPosition.get("dev"));
        assertEquals(new Person("John", "Doe", 24), coolestByPosition.get("BA"));
    }

//     With the longest sum duration on this position
//     { John Doe, [{dev, google, 4}, {dev, epam, 4}] } предпочтительнее, чем { A B, [{dev, google, 6}, {QA, epam, 100}]}
    private Map<String, Person> getCoolestByPosition2(List<Employee> employees) {
        Map<String, List<PersonPositionDuration>> map = employees.stream()
            .flatMap(this::positionDuration)
            .collect(groupingBy(PersonPositionDuration::getPosition));

        return map.entrySet().stream()
            .collect(toMap(Map.Entry::getKey,
                           ppd -> ppd.getValue().stream()
            .reduce((ppd1, ppd2) -> ppd1.getDuration() > ppd2.getDuration() ? ppd1 : ppd2)
            .get()
            .getPerson()));
    }

    private Stream<PersonPositionDuration> positionDuration(Employee employee){
        Map<String, Integer> mapEmployee = employee.getJobHistory().stream()
            .collect(groupingBy(JobHistoryEntry::getPosition,
                                summingInt(JobHistoryEntry::getDuration)));

        return mapEmployee.entrySet().stream()
            .map(e -> new PersonPositionDuration(employee.getPerson(), e.getKey(), e.getValue()));
    }

        private List<Employee> getEmployees () {
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
