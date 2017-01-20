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

        employees.stream()
                .flatMap(CollectorsExercise1::employeeInfo)
                .collect(Collectors.groupingBy(PersonPositionDuration::getPosition, Collectors.collectingAndThen(
                        Collectors.maxBy(
                                Comparator.comparingInt(PersonPositionDuration::getDuration)),
                                info -> info.get().getPerson())));
        employees.stream()
                .flatMap(CollectorsExercise1::employeeInfo)
                .collect(new Collector<PersonPositionDuration, Map<String,PersonPositionDuration>, Map<String,Person>>() {
                    @Override
                    public Supplier<Map<String, PersonPositionDuration>> supplier() {
                        return HashMap::new;
                    }

                    @Override
                    public BiConsumer<Map<String, PersonPositionDuration>, PersonPositionDuration> accumulator() {
                        return (map, info) -> map.merge(
                                info.getPosition(),
                                info,
                                (ppd1,ppd2) -> ppd2.getDuration() > ppd1.getDuration() ? ppd2 : ppd1);
                    }

                    @Override
                    public BinaryOperator<Map<String, PersonPositionDuration>> combiner() {
                        return (map1,map2) -> {
                            for (PersonPositionDuration info : map2.values()){
                                PersonPositionDuration mapInfo = map1.get(info.getPosition());
                                if (mapInfo == null || info.getDuration() > mapInfo.getDuration()){
                                    map1.put(info.getPosition(),info);
                                }
                            }
                            return map1;
                        };
                    }

                    @Override
                    public Function<Map<String, PersonPositionDuration>, Map<String, Person>> finisher() {
                        return mapInfo -> mapInfo
                                .entrySet()
                                .stream()
                                .map(Map.Entry::getValue)
                                .collect(Collectors.toMap(
                                        PersonPositionDuration::getPosition,
                                        PersonPositionDuration::getPerson));
                    }

                    @Override
                    public Set<Characteristics> characteristics() {
                        return Collections.emptySet();
                    }
                });

        result = getCoolestByPositionFromStream(employees.stream().flatMap(CollectorsExercise1::employeeInfo));
        return result;
    }

    private static Stream<PersonPositionDuration> employeeInfo(Employee employee) {
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

    private static Map<String,Person> getCoolestByPositionFromStream(Stream<PersonPositionDuration> upstream){
        return upstream
                .collect(Collectors.toMap(PersonPositionDuration::getPosition,Function.identity(),
                        (ppd1,ppd2) -> ppd2.getDuration() > ppd1.getDuration() ? ppd2 : ppd1))
                .entrySet()
                .stream()
                .collect(Collectors.toMap(Map.Entry::getKey,entry -> entry.getValue().getPerson()));
    }

    private static Stream<PersonPositionDuration> employeeInfoDuration(Employee employee) {
        return employeeInfo(employee)
                .collect(Collectors.groupingBy(PersonPositionDuration::getPosition))
                .entrySet()
                .stream()
                .map(entry -> new PersonPositionDuration(
                        employee.getPerson(),
                        entry.getKey(),
                        entry.getValue().stream().mapToInt(PersonPositionDuration::getDuration).sum()));
    }

    private Map<String, Person> getCoolestByPosition2(List<Employee> employees) {
        return getCoolestByPositionFromStream(employees.stream()
                .flatMap(CollectorsExercise1::employeeInfoDuration));

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