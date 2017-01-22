package part1.exercise;

import data.Employee;
import data.JobHistoryEntry;
import data.Person;
import org.junit.Test;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;
import static org.junit.Assert.assertEquals;

public class StreamsExercise2 {
    // https://youtu.be/kxgo7Y4cdA8 Сергей Куксенко и Алексей Шипилёв — Через тернии к лямбдам, часть 1
    // https://youtu.be/JRBWBJ6S4aU Сергей Куксенко и Алексей Шипилёв — Через тернии к лямбдам, часть 2

    // https://youtu.be/O8oN4KSZEXE Сергей Куксенко — Stream API, часть 1
    // https://youtu.be/i0Jr2l3jrDA Сергей Куксенко — Stream API, часть 2

    // TODO class PersonEmployerPair
    private static class PersonEmployerPair {
        private Person person;
        private String employer;

        public PersonEmployerPair(Person person, String employer) {
            this.person = person;
            this.employer = employer;
        }

        public Person getPerson() {
            return person;
        }

        public String getEmployer() {
            return employer;
        }
    }

    private static Stream<PersonEmployerPair> newEmployerPairs (Employee e) {
        return e.getJobHistory().stream().map(JobHistoryEntry::getEmployer).map(j -> new PersonEmployerPair(e.getPerson(), j));
    }
    private static Stream<PersonEmployerPair> personEmployerPairStream (Employee e) {
        return e.getJobHistory().stream().limit(1).map(JobHistoryEntry::getEmployer).map(p -> new StreamsExercise2.PersonEmployerPair(e.getPerson(), p));
    }

    @Test
    public void employersStuffLists() {
        Map<String, List<Person>> employersStuffLists = getEmployees().stream().
                flatMap(StreamsExercise2::newEmployerPairs).
                collect(Collectors.groupingBy(PersonEmployerPair::getEmployer, Collectors.mapping(PersonEmployerPair::getPerson, toList())));
        final List<Employee> employees = getEmployees();
        final Map<String, List<Person>> map = new HashMap<>();
        for (Employee e : employees) for (JobHistoryEntry j : e.getJobHistory())
        {
            map.putIfAbsent(j.getEmployer(),new ArrayList<>());
            map.merge(j.getEmployer(),Collections.singletonList(e.getPerson()), (p1,p2) -> {
                p1.addAll(p2);
                return p1;
            });
        }
        assertEquals(map, employersStuffLists);
    }


    private static class DurationOfEmployerPerson {
        private String employer;
        private Person person;
        private int duration;

        public DurationOfEmployerPerson(String employer, Person person, int duration) {
            this.employer = employer;
            this.person = person;
            this.duration = duration;
        }

        public String getEmployer() {
            return employer;
        }

        public Person getPerson() {
            return person;
        }

        public int getDuration() {
            return duration;
        }
    }

    private static Stream<DurationOfEmployerPerson> jobHistoryToDuration(Employee e) {
        return e.getJobHistory().stream().map(j -> new DurationOfEmployerPerson(j.getEmployer(),e.getPerson(),j.getDuration()));
    }

    @Test
    public void indexByFirstEmployer() {
        List<Employee> employees = getEmployees();
        Map<String,List<Person>> map = new HashMap<>();
        for (Employee e : employees) {
            map.putIfAbsent(e.getJobHistory().get(0).getEmployer(), new ArrayList<>());
            map.merge(e.getJobHistory().get(0).getEmployer(), Collections.singletonList(e.getPerson()), (p1,p2) -> {
                p1.addAll(p2);
                return p1;
            });
        }

        Map<String, List<Person>> employeesIndex = employees.stream().flatMap(e -> newEmployerPairs(e).limit(1)).
                collect(Collectors.groupingBy(PersonEmployerPair::getEmployer, Collectors.mapping(PersonEmployerPair::getPerson, Collectors.toList())));
        assertEquals(map,employeesIndex);
    }

    @Test
    public void greatestExperiencePerEmployer() {
        Map<String, Person> employeesIndex = getEmployees().stream().flatMap(StreamsExercise2::jobHistoryToDuration)
                .collect(Collectors.groupingBy(DurationOfEmployerPerson::getEmployer, Collectors.collectingAndThen(Collectors.
                                maxBy(Comparator.comparingInt(DurationOfEmployerPerson::getDuration)), j -> j.get().getPerson())));

        assertEquals(new Person("John", "White", 28), employeesIndex.get("epam"));
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
                                new JobHistoryEntry(666, "BA", "epam")
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
