package part1.example;

import data.Employee;
import data.JobHistoryEntry;
import data.Person;
import org.junit.Test;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.*;
import static org.junit.Assert.assertEquals;

public class StreamsExample {

    // https://github.com/java8-course/streams

    private static Stream<PersonPositionPair> employeeToPairs(Employee employee) {
        return employee.getJobHistory().stream()
                .map(JobHistoryEntry::getPosition)
                .map(p -> new PersonPositionPair(employee.getPerson(), p));
    }

    // StreamsExercise1

    @Test
    public void checkJohnsLastNames() {
        final List<Employee> employees = getEmployees();

        final List<String> johnsLastNames =
                employees.stream()
                        .map(Employee::getPerson)
                        .filter(e -> e.getFirstName().equals("John"))
                        .map(Person::getLastName)
                        .distinct()
                        .collect(toList());

        assertEquals(Arrays.asList("Galt", "Doe", "White"), johnsLastNames);
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
                .sorted(comparing(JobHistoryEntry::getDuration))
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
                .sorted(comparing(JobHistoryEntry::getDuration))
                .forEachOrdered(System.out::println);
    }

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

    // [ (John, [dev, QA]), (Bob, [QA, QA])] -> [dev -> [John], QA -> [John, Bob]]
    // [ (John, dev), (John, QA), (Bob, QA), (Bob, QA)]
    private Map<String, Set<Person>> getPositionIndex(List<Employee> employees) {
        final Stream<PersonPositionPair> personPositionPairStream = employees.stream()
                .flatMap(StreamsExample::employeeToPairs);

        //personPositionPairStream
        //        .reduce(Collections.EMPTY_MAP, StreamsExample::addToMap, StreamsExample::combineMaps);

//        return personPositionPairStream
//                .collect(
//                        () -> new HashMap<>(),
//                        (m, p) -> {
//                            final Set<Person> set = m.computeIfAbsent(p.getPosition(), (k) -> new HashSet<>());
//                            set.add(p.getPerson());
//                        },
//                        (m1, m2) -> {
//                            for (Map.Entry<String, Set<Person>> entry : m2.entrySet()) {
//                                Set<Person> set = m1.computeIfAbsent(entry.getKey(), (k) -> new HashSet<>());
//                                set.addAll(entry.getValue());
//                            }
//                        });
        return personPositionPairStream
                .collect(Collectors.groupingBy(
                        PersonPositionPair::getPosition,
                        mapping(PersonPositionPair::getPerson, toSet())));

    }

/*    private static Map<String, Set<Person>> combineMaps(Map<String, Set<Person>> u1,
                                                        Map<String, Set<Person>> u2) {
        final HashMap<String, Set<Person>> result = new HashMap<>();
        result.putAll(u1);
        for (Map.Entry<String, Set<Person>> entry : u2.entrySet()) {
            Set<Person> set = result.computeIfAbsent(entry.getKey(), (k) -> new HashSet<>());
            set.addAll(entry.getValue());
        }
        return result;
    }

    private static Map<String, Set<Person>> addToMap(
            Map<String, Set<Person>> u,
            PersonPositionPair personPositionPair) {
        final HashMap<String, Set<Person>> result = new HashMap<>();
        result.putAll(u);
        Set<Person> set = result.computeIfAbsent(personPositionPair.getPosition(), (k) -> new HashSet<>());
        set.add(personPositionPair.getPerson());
        return result;
    }*/


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

    private Map<String, Person> getCoolestByPosition(List<Employee> employees) {
        final Stream<PersonPositionDuration> personPositionDurationStream = employees.stream()
                .flatMap(
                        e -> e.getJobHistory()
                                .stream()
                                .map(j -> new PersonPositionDuration(e.getPerson(), j.getPosition(), j.getDuration())));
//        final Map<String, PersonPositionDuration> collect = personPositionDurationStream
//                .collect(toMap(
//                        PersonPositionDuration::getPosition,
//                        Function.identity(),
//                        (p1, p2) -> p1.getDuration() > p2.getDuration() ? p1 : p2));
        return personPositionDurationStream
                .collect(groupingBy(
                        PersonPositionDuration::getPosition,
                        collectingAndThen(
                                maxBy(comparing(PersonPositionDuration::getDuration)), p -> p.get().getPerson())));
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
        //final Employee[] employeesArray = getEmployees().stream().toArray(Employee[]::new);
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

}
