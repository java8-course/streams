package part2.exercise;

import data.Employee;
import data.Job;
import data.JobHistoryEntry;
import data.Person;
import org.junit.Test;

import java.util.*;

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

        Map<String, Person> result = employees.stream()
                .flatMap(emp -> emp.getJobHistory()
                        .stream()
                        .map(job -> new Job(emp.getPerson(), job.getEmployer(), job.getDuration(), job.getPosition())))
                .collect(groupingBy(Job::getPosition,
                        collectingAndThen(
                                maxBy(Comparator.comparing(Job::getDuration)), res -> res.get().getPerson())));

        return result;

//        Map<String, Person> result = employees.stream()
//                .flatMap(emp -> emp.getJobHistory()
//                        .stream()
//                        .map(job -> new Job(emp.getPerson(), job.getEmployer(), job.getDuration(), job.getPosition())))
//                .collect(toMap(Job::getPosition, Function.identity(),
//                        (el1, el2) -> el1.getDuration() > el2.getDuration() ? el1 : el2))
//                .entrySet()
//                .stream()
//                .collect(toMap(Map.Entry::getKey, e -> e.getValue().getPerson()));
//
//        return result;

//        return employees.stream()
//                .flatMap(emp -> emp.getJobHistory()
//                        .stream()
//                        .map(job -> new Job(emp.getPerson(), job.getEmployer(), job.getDuration(), job.getPosition()))
//                        .sorted(Comparator.comparing(Job::getDuration)))
//                .collect(new Collector<Job, Map<String, Job>, Map<String, Person>>() {
//                    @Override
//                    public Supplier<Map<String, Job>> supplier() {
//                        return HashMap::new;
//                    }
//
//                    @Override
//                    public BiConsumer<Map<String, Job>, Job> accumulator() {
//                        return (acc, el) ->
//                                acc.merge(el.getPosition(), el,
//                                        (key, old) -> old.getDuration() < el.getDuration() ? el : old);
//                    }
//
//                    @Override
//                    public BinaryOperator<Map<String, Job>> combiner() {
//                        return (map1, map2) -> {
//                            map2.forEach((key1, value) -> map1.merge(key1, value,
//                                    (key, old) -> old.getDuration() < value.getDuration() ? value : old));
//                            return map1;
//                        };
//                    }
//
//                    @Override
//                    public Function<Map<String, Job>, Map<String, Person>> finisher() {
//                        return (map) -> {
//                            Map<String, Person> result1 = new HashMap<>();
//                            map.forEach((k, v) -> {
//                                result1.put(k, v.getPerson());
//                            });
//                            return result1;
//                        };
//                    }
//
//                    @Override
//                    public Set<Characteristics> characteristics() {
//                        return Collections.emptySet();
//                    }
//                });

    }

    @Test
    public void getTheCoolestOne2() {
        final Map<String, Person> coolestByPosition = getCoolestByPosition2(getEmployees());

        coolestByPosition.forEach((position, person) -> System.out.println(position + " -> " + person));
    }

    // With the longest sum duration on this position
    // { John Doe, [{dev, google, 4}, {dev, epam, 4}] } предпочтительнее, чем { A B, [{dev, google, 6}, {QA, epam, 100}]}
    private Map<String, Person> getCoolestByPosition2(List<Employee> employees) {

        Map<String, Person> result = employees.stream()
                .flatMap(emp -> emp.getJobHistory()
                        .stream()
                        .map(job -> new Job(emp.getPerson(), job.getEmployer(), job.getDuration(), job.getPosition())))
                .collect(groupingBy(Job::getPosition,
                        collectingAndThen(
                                groupingBy(Job::getPerson, reducing(
                                        (p1, p2) -> new Job(p1.getPerson(), p1.getEmployer(),
                                                p1.getDuration() + p2.getDuration(), p1.getPosition()))),
                                CollectorsExercise1::findMaxJob)));

        return result;
    }

    private static Person findMaxJob(Map<Person, Optional<Job>> map) {
        return map.entrySet()
                .stream()
                .max(Comparator.comparing(ent -> ent.getValue().get().getDuration()))
                .get()
                .getKey();

    }

    private List<Employee> getEmployees() {
        return Arrays.asList(
                new Employee(
                        new Person("John", "GaltTop", 20),
                        Arrays.asList(
                                new JobHistoryEntry(30, "dev", "epam"),
                                new JobHistoryEntry(2, "dev", "google")
                        )),
                new Employee(
                        new Person("John", "DoeTop", 21),
                        Arrays.asList(
                                new JobHistoryEntry(40, "BA", "yandex"),
                                new JobHistoryEntry(2, "QA", "epam"),
                                new JobHistoryEntry(2, "dev", "abc")
                        )),
                new Employee(
                        new Person("John", "WhiteTop", 22),
                        Collections.singletonList(
                                new JobHistoryEntry(60, "QA", "epam")
                        )),
                new Employee(
                        new Person("John", "GaltTotal", 23),
                        Arrays.asList(
                                new JobHistoryEntry(30, "dev", "epam"),
                                new JobHistoryEntry(20, "dev", "google")
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
                        new Person("Bob", "DoeTotal", 27),
                        Arrays.asList(
                                new JobHistoryEntry(40, "QA", "yandex"),
                                new JobHistoryEntry(25, "QA", "epam"),
                                new JobHistoryEntry(2, "dev", "abc")
                        )),
                new Employee(
                        new Person("John", "WhiteTotal", 28),
                        Arrays.asList(
                                new JobHistoryEntry(26, "BA", "epam"),
                                new JobHistoryEntry(36, "BA", "epam")
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
