package part1.exercise;

import data.Employee;
import data.JobHistoryEntry;
import data.Person;
import org.junit.Test;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.Assert.assertEquals;

public class StreamsExercise2 {
    // https://youtu.be/kxgo7Y4cdA8 Сергей Куксенко и Алексей Шипилёв — Через тернии к лямбдам, часть 1
    // https://youtu.be/JRBWBJ6S4aU Сергей Куксенко и Алексей Шипилёв — Через тернии к лямбдам, часть 2

    // https://youtu.be/O8oN4KSZEXE Сергей Куксенко — Stream API, часть 1
    // https://youtu.be/i0Jr2l3jrDA Сергей Куксенко — Stream API, часть 2

    private static class PersonEmployerPair{
        private final Person person;
        private final String employer;

        public Person getPerson() {
            return person;
        }

        public String getEmployer() {
            return employer;
        }

        public PersonEmployerPair(Person person, String employer) {
            this.person = person;
            this.employer = employer;
        }
    }

    @Test
    public void employersStuffLists() {

        final List<Employee> employees = getEmployees();
        final Map<String,List<Person>> expected = new HashMap<>();
        for (Employee e : employees){
            for (JobHistoryEntry job : e.getJobHistory()){
                expected.putIfAbsent(job.getEmployer(),new ArrayList<>());
                expected.merge(job.getEmployer(), Collections.singletonList(e.getPerson()), (persons, persons2) -> {
                    persons.addAll(persons2);
                    return persons;
                });
            }
        }

        Map<String, List<Person>> employersStuffLists = employees.stream()
                .flatMap(StreamsExercise2::makePersonEmployerPairs)
                .collect(Collectors.groupingBy(PersonEmployerPair::getEmployer,
                        Collectors.mapping(PersonEmployerPair::getPerson,Collectors.toList())));

        assertEquals(expected,employersStuffLists);
    }

    private static Stream<PersonEmployerPair> makePersonEmployerPairs(Employee e){
        return e.getJobHistory().stream()
                .map(JobHistoryEntry::getEmployer)
                .map(employer -> new PersonEmployerPair(e.getPerson(),employer));
    }


    @Test
    public void indexByFirstEmployer() {
        List<Employee> employees = getEmployees();
        Map<String,List<Person>> expected = new HashMap<>();
        for (Employee e : employees){
            if (e.getJobHistory() != null && !e.getJobHistory().isEmpty()){
                JobHistoryEntry job = e.getJobHistory().get(0);
                expected.putIfAbsent(job.getEmployer(),new ArrayList<>());
                expected.merge(job.getEmployer(), Collections.singletonList(e.getPerson()), (persons, persons2) -> {
                    persons.addAll(persons2);
                    return persons;
                });
            }
        }
        Map<String, List<Person>> employeesIndex = employees.stream()
                .flatMap(e -> makePersonEmployerPairs(e).limit(1))
                .collect(Collectors.groupingBy(PersonEmployerPair::getEmployer,
                        Collectors.mapping(PersonEmployerPair::getPerson,Collectors.toList())));
        assertEquals(expected,employeesIndex);
    }


    private static class EmployerPersonDuration {
        private final String employer;
        private final Person person;
        private final int duration;

        public EmployerPersonDuration(String employer, Person person, int duration) {
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


    @Test
    public void greatestExperiencePerEmployer() {

        List<Employee> employees = getEmployees();

        Map<String, Person> employeesIndex = employees.stream()
                .flatMap(StreamsExercise2::getEmployeeExperience)
                .collect(Collectors.groupingBy(EmployerPersonDuration::getEmployer,
                        Collectors.collectingAndThen(Collectors
                                .maxBy(Comparator.comparingInt(EmployerPersonDuration::getDuration)),
                                    triple -> triple.get().getPerson())));

        assertEquals(new Person("John", "White", 28), employeesIndex.get("epam"));
    }

    private static Stream<EmployerPersonDuration> getEmployeeExperience(Employee employee) {
        return employee.getJobHistory().stream()
                .map(entry -> new EmployerPersonDuration(entry.getEmployer(),employee.getPerson(),entry.getDuration()));
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
