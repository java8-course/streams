package part1.exercise;

import data.Employee;
import data.JobHistoryEntry;
import data.Person;
import org.junit.Test;

import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;

import static java.util.Comparator.comparingInt;
import static java.util.stream.Collectors.*;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class StreamsExercise2 {
    // https://youtu.be/kxgo7Y4cdA8 Сергей Куксенко и Алексей Шипилёв — Через тернии к лямбдам, часть 1
    // https://youtu.be/JRBWBJ6S4aU Сергей Куксенко и Алексей Шипилёв — Через тернии к лямбдам, часть 2

    // https://youtu.be/O8oN4KSZEXE Сергей Куксенко — Stream API, часть 1
    // https://youtu.be/i0Jr2l3jrDA Сергей Куксенко — Stream API, часть 2
    private final static Predicate<Employee> hasJob = s -> !s.getJobHistory().isEmpty();

    private static class PersonEmployerPair {
        private final Person person;
        private final String employee;


        private PersonEmployerPair(Person person, String employee) {
            this.person = person;
            this.employee = employee;
        }

        Person getPerson() {
            return person;
        }

        String getEmployee() {
            return employee;
        }
    }
    @Test
    public void employersStuffLists() {
        List<Employee> initialList = getEmployees();
        Map<String, List<Person>> actualMap = initialList.stream()
                .flatMap(this::toPersonEmployerPairStream)
                .collect(groupingBy(PersonEmployerPair::getEmployee,
                        mapping(PersonEmployerPair::getPerson, toList())));
        Map<String, List<Person>> expectedMap = new HashMap<>();

        List<String> employers = Arrays.asList("epam", "yandex", "google", "abc");
        for (String employer : employers) {
            List<Person> personList = new ArrayList<>();
            for (Employee employee : initialList) {
                if (employee.getJobHistory().stream()
                        .map(JobHistoryEntry::getEmployer)
                        .anyMatch(s -> s.equals(employer))) {
                    personList.add(employee.getPerson());
                }
            }
            expectedMap.put(employer, personList);
        }

        assertThat(actualMap, is(expectedMap));
    }

    private Stream<PersonEmployerPair> toPersonEmployerPairStream(Employee s) {
        return s.getJobHistory().stream().map(g -> new PersonEmployerPair(s.getPerson(), g.getEmployer()));
    }

    @Test
    public void indexByFirstEmployer() {
        final List<Employee> initialList = getEmployees();

        final Function<Employee, PersonEmployerPair> toPersonEmployerPair =
                s -> new PersonEmployerPair(s.getPerson(), s.getJobHistory().get(0).getEmployer());

        Map<String, List<Person>> actualMap = getEmployees().stream()
                .filter(hasJob)
                .map(toPersonEmployerPair)
                .collect(groupingBy(PersonEmployerPair::getEmployee, mapping(PersonEmployerPair::getPerson, toList())));

        Map<String, List<Person>> expectedMap = new HashMap<>();

        List<String> employers = Arrays.asList("epam", "yandex", "google", "abc");
        for (String employer : employers) {
            List<Person> personList = new ArrayList<>();
            for (Employee employee : initialList) {
                if (employee.getJobHistory().stream()
                        .findFirst()
                        .map(JobHistoryEntry::getEmployer)
                        .filter(s -> s.equals(employer))
                        .map(s -> true)
                        .orElse(false)) {
                    personList.add(employee.getPerson());
                }
            }
            if (!personList.isEmpty()) expectedMap.put(employer, personList);
        }

        assertThat(actualMap, is(expectedMap));
    }

    private static class PersonEmployerDuration {
        private final Person person;
        private final String employer;
        private final int duration;

        private PersonEmployerDuration(Person person, String employer, int duration) {
            this.person = person;
            this.employer = employer;
            this.duration = duration;
        }

        Person getPerson() {
            return person;
        }

        String getEmployer() {
            return employer;
        }

        int getDuration() {
            return duration;
        }
    }

    @Test
    public void greatestExperiencePerEmployer() {

        List<Employee> initialList = getEmployees();
        Map<String, Person> employeesIndex = initialList.stream()
                .filter(hasJob)
                .flatMap(this::toPersonEmployerDurationStream)
                .collect(groupingBy(PersonEmployerDuration::getEmployer,
                        collectingAndThen(maxBy(comparingInt(PersonEmployerDuration::getDuration)),
                                g -> g.get().getPerson())));

        Map<String, Person> expected = new HashMap<>();

        expected.put("abc", new Person("John", "Doe", 30));
        expected.put("yandex", new Person("John", "Doe", 21));
        expected.put("epam", new Person("John", "White", 28));
        expected.put("google", new Person("John", "Galt", 20));

        assertThat(employeesIndex, is(expected));
    }

    private Stream<PersonEmployerDuration> toPersonEmployerDurationStream(Employee s) {
        return s.getJobHistory().stream().map(j->new PersonEmployerDuration(s.getPerson(),j.getEmployer(),j.getDuration()));
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
