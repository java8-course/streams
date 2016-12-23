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

        final Map<String,Person> result;

        // method #1
        employees.stream()
                .flatMap(CollectorsExercise1::employeeToTriples)
                .collect(Collectors.groupingBy(PersonPositionDuration::getPosition,
                        Collectors.collectingAndThen(
                                Collectors.maxBy(Comparator.comparingInt(PersonPositionDuration::getDuration)),
                                triple -> triple.get().getPerson())));
        // method #2
        employees.stream()
                .flatMap(CollectorsExercise1::employeeToTriples)
                .collect(new Collector<PersonPositionDuration, Map<String,PersonPositionDuration>, Map<String,Person>>() {
                    @Override
                    public Supplier<Map<String, PersonPositionDuration>> supplier() {
                        return HashMap::new;
                    }

                    @Override
                    public BiConsumer<Map<String, PersonPositionDuration>, PersonPositionDuration> accumulator() {
                        return (map, triple) -> {
                            PersonPositionDuration tripleInMap = map.get(triple.getPosition());
                            if (tripleInMap == null || triple.getDuration() > tripleInMap.getDuration()){
                                map.put(triple.getPosition(),triple);
                            }
                        };
                    }

                    @Override
                    public BinaryOperator<Map<String, PersonPositionDuration>> combiner() {
                        return (map1,map2) -> {
                            for (PersonPositionDuration triple : map2.values()){
                                PersonPositionDuration tripleInMap1 = map1.get(triple.getPosition());
                                if (tripleInMap1 == null || triple.getDuration() > tripleInMap1.getDuration()){
                                    map1.put(triple.getPosition(),triple);
                                }
                            }
                            return map1;
                        };
                    }

                    @Override
                    public Function<Map<String, PersonPositionDuration>, Map<String, Person>> finisher() {
                        return triplesMap -> {
                            Map<String,Person> result = new HashMap<>();
                            for (PersonPositionDuration triple : triplesMap.values()){
                                result.put(triple.getPosition(),triple.getPerson());
                            }
                            return result;
                        };
                    }

                    @Override
                    public Set<Characteristics> characteristics() {
                        return Collections.emptySet();
                    }
                });

        // method #3
        result = employees.stream()
                .flatMap(CollectorsExercise1::employeeToTriples)
                .collect(Collectors.toMap(PersonPositionDuration::getPosition,Function.identity(),
                        (ppd1,ppd2) -> ppd2.getDuration() > ppd1.getDuration() ? ppd2 : ppd1))
                .entrySet()
                .stream()
                .collect(Collectors.toMap(Map.Entry::getKey,entry -> entry.getValue().getPerson()));
        return result;
    }

    private static Stream<PersonPositionDuration> employeeToTriples(Employee employee) {
        return employee.getJobHistory().stream()
                .map(entry -> new PersonPositionDuration(employee.getPerson(),
                        entry.getPosition(),
                        entry.getDuration()));
    }

    @Test
    public void getTheCoolestOne2() {
        final Map<String, Person> coolestByPosition = getCoolestByPosition2(getEmployees());
        coolestByPosition.forEach((position, person) -> System.out.println(position + " -> " + person));
    }

    // With the longest sum duration on this position
    // { John Doe, [{dev, google, 4}, {dev, epam, 4}] } предпочтительнее, чем { A B, [{dev, google, 6}, {QA, epam, 100}]}
    private Map<String, Person> getCoolestByPosition2(List<Employee> employees) {
        return employees.stream()
                .flatMap(CollectorsExercise1::employeeToTriples)
                .collect(Collectors.groupingBy(PersonPositionDuration::getPosition,
                        Collectors.groupingBy(PersonPositionDuration::getPerson,
                                HashMap::new,Collectors.summingInt(PersonPositionDuration::getDuration))))
                .entrySet()
                .stream()
                .collect(Collectors.toMap(Map.Entry::getKey,
                        // in maps on each position we find max experience
                        entry -> entry.getValue().entrySet().stream().max(Comparator.comparingInt(Map.Entry::getValue)).get().getKey()));

    }

    private List<Employee> getEmployees() {
        return Arrays.asList(
                new Employee(
                        new Person("John", "Galt", 20),
                        Arrays.asList(
                                new JobHistoryEntry(3, "dev", "epam"),
                                new JobHistoryEntry(3, "dev", "google")
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
                                new JobHistoryEntry(5, "QA", "epam"),
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
