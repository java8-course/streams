package part1.example;

import data.Employee;
import data.JobHistoryEntry;
import data.Person;
import org.junit.Test;
import part1.exercise.StreamsExercise1;

import java.util.*;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.*;
import static org.junit.Assert.assertEquals;

public class StreamsExample {

    // https://github.com/senia-psm/java-streams

    @Test
    public void checkJohnsLastNames() {
        final List<Employee> employees = getEmployees();

        final List<String> johnsLastNames =
                employees.stream()
                        .map(Employee::getPerson)
                        .filter(e -> e.getFirstName().equals("John"))
                        .map(Person::getLastName)
                        .distinct()
                        .collect(Collectors.toList());

        assertEquals(Collections.singletonList("Galt"), johnsLastNames);
    }

    /**
     * Промежуточные операсции
     */
    @Test
    public void operations() {
        final List<Employee> employees = getEmployees();

        final Optional<JobHistoryEntry> jobHistoryEntry = employees.stream()
                .filter(e -> e.getPerson().getFirstName().equals("John"))
                .map(Employee::getJobHistory)
                .flatMap(Collection::stream)
                .peek(System.out::println)
                .distinct()
                .sorted(Comparator.comparing(JobHistoryEntry::getDuration))
                .skip(1) // long
                .limit(10) // long
                .unordered()
                .parallel()
                .sequential()
                .findAny();
        //      .allMatch(Predicate<T>)
        //      .anyMatch(Predicate<T>)
        //      .noneMatch(Predicate<T>)
        //      .reduce(BinaryOperator<T>) // ассоциативная операция
        //      .collect(Collector<T, A, R>)
        //      .count()
        //      .findAny()
        //      .findFirst()
        //      .forEach(Consumer<T>)
        //      .forEachOrdered(Consumer<>)
        //      .max()
        //      .min()
        //      .toArray(IntFunction<A[]>)
        //      .iterator()

        // Characteristic :
        // CONCURRENT
        // DISTINCT
        // IMMUTABLE
        // NONNULL
        // ORDERED
        // SIZED
        // SORTED
        // SUBSIZED


        System.out.println(jobHistoryEntry);
    }

    @Test
    public void checkAgedJohnsExpiriences() {
        final List<Employee> employees = getEmployees();

        // Every aged (>= 25) John has an odd "dev" job experience

        employees.stream()
                .filter(e -> e.getPerson().getFirstName().equals("John"))
                .filter(e -> e.getPerson().getAge() >= 25)
                .flatMap(e -> e.getJobHistory().stream())
                .filter(e -> e.getPosition().equals("dev"))
                .distinct()
                .sorted(Comparator.comparing(JobHistoryEntry::getDuration))
                .forEachOrdered(System.out::println);
    }

    // StreamsExercise1

    @Test
    public void getProfessionals() {
        final Map<String, Set<Person>> positionIndex = getPositionIndex(getEmployees());

        for (Person person : positionIndex.get("dev")) {
            System.out.println(person);
        }

        for (Person person : positionIndex.get("QA")) {
            System.out.println(person);
        }

        positionIndex.get("BA").forEach(System.out::println);
    }

    private static class PersonPositionPair {
        private final Person person;
        private final String position;

        public PersonPositionPair(Person person, String position) {
            this.person = person;
            this.position = position;
        }

        public Person getPerson() {
            return person;
        }

        public String getPosition() {
            return position;
        }
    }

    private Map<String, Set<Person>> getPositionIndex(List<Employee> employees) {
        return employees.stream()
                .flatMap(e ->
                        e.getJobHistory().stream()
                                .map(j -> new PersonPositionPair(e.getPerson(), j.getPosition()))
                )
                .collect(groupingBy(PersonPositionPair::getPosition,
                        mapping(PersonPositionPair::getPerson, toSet())));
        //groupingBy, mapping
    }

    @Test
    public void getTheCoolestOne() {
        final Map<String, Person> coolestByPosition = getCoolestByPosition(getEmployees());

        coolestByPosition.forEach((position, person) -> System.out.println(position + " -> " + person));
    }

    private Map<String, Person> getCoolestByPosition(List<Employee> employees) {
        // TODO
        throw new UnsupportedOperationException();
    }

    @Test
    public void intStream_bad1() {
        int sumDuration =
                getEmployees().stream()
                        .flatMap(
                                employee -> employee.getJobHistory().stream()
                        )
                        .collect(mapping(JobHistoryEntry::getDuration, Collectors.reducing(0, (a, b) -> a + b)));

        System.out.println("sum: " + sumDuration);
    }

    @Test
    public void intStream_bad2() {
        int sumDuration =
                getEmployees().stream()
                        .flatMap(
                                employee -> employee.getJobHistory().stream()
                        )
                        .map(JobHistoryEntry::getDuration)
                        .collect(Collectors.reducing(0, (a, b) -> a + b));

        System.out.println("sum: " + sumDuration);
    }


    @Test
    public void intStream_bad3() {
        int sumDuration =
                getEmployees().stream()
                        .flatMap(
                                employee -> employee.getJobHistory().stream()
                        )
                        .collect(Collectors.summingInt(JobHistoryEntry::getDuration));

        System.out.println("sum: " + sumDuration);
    }

    @Test
    public void intStream() {
        final int sumDuration =
                getEmployees().stream()
                        .flatMap(employee -> employee.getJobHistory().stream())
                        .mapToInt(JobHistoryEntry::getDuration)
                        .sum();

        System.out.println("sum: " + sumDuration);
    }

    @Test
    public void intStream_array() {
        final Employee[] employeesArray = getEmployees().toArray(new Employee[0]);
        final int sumDuration =
                Arrays.stream(employeesArray)
                        .flatMap(employee -> employee.getJobHistory().stream())
                        .mapToInt(JobHistoryEntry::getDuration)
                        .sum();

        System.out.println("sum: " + sumDuration);
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
