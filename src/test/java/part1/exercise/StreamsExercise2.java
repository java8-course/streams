package part1.exercise;

import data.Employee;
import data.JobHistoryEntry;
import data.Person;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.junit.Test;

import java.util.*;

import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.*;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

public class StreamsExercise2 {
    // https://youtu.be/kxgo7Y4cdA8 Сергей Куксенко и Алексей Шипилёв — Через тернии к лямбдам, часть 1
    // https://youtu.be/JRBWBJ6S4aU Сергей Куксенко и Алексей Шипилёв — Через тернии к лямбдам, часть 2

    // https://youtu.be/O8oN4KSZEXE Сергей Куксенко — Stream API, часть 1
    // https://youtu.be/i0Jr2l3jrDA Сергей Куксенко — Stream API, часть 2

    @AllArgsConstructor
    @Getter
    private class PersonEmployerPair {
        private final Person person;
        private final String employer;
    }

    @Test
    public void employersStuffLists() {
        final Map<String, List<Person>> expected = new HashMap<>();
        {   // Expected
            for (Employee e : getEmployees()) {
                final Person p = e.getPerson();
                for (JobHistoryEntry jobHistoryEntry : e.getJobHistory()) {
                    final String employer = jobHistoryEntry.getEmployer();
                    List<Person> eStaff = expected.get(employer);
                    if (eStaff == null) {                       // We're collecting staff list, not stuff list :)
                        eStaff = new ArrayList<>();
                        expected.put(employer, eStaff);
                    }
                    if (!eStaff.contains(p)) eStaff.add(p);
                }
            }
        }

        final Map<String, List<Person>> employersStuffLists = getEmployees().stream()
                .flatMap(e -> e.getJobHistory().stream()
                        .map(JobHistoryEntry::getEmployer)
                        .map(emp -> new PersonEmployerPair(e.getPerson(), emp)))
                .distinct()
                .collect(groupingBy(PersonEmployerPair::getEmployer, mapping(PersonEmployerPair::getPerson, toList())));

        assertThat(employersStuffLists.entrySet(), containsInAnyOrder(expected.entrySet().toArray()));
    }

    @Test
    public void indexByFirstEmployer() {
        final Map<String, List<Person>> expected = new HashMap<>();
        {   // Expected
            for (Employee e : getEmployees()) {
                final Person p = e.getPerson();
                final String firstEmployer = e.getJobHistory().get(0).getEmployer();
                List<Person> eStaff = expected.get(firstEmployer);
                if (eStaff == null) {
                    eStaff = new ArrayList<>();
                    expected.put(firstEmployer, eStaff);
                }
                eStaff.add(p);
            }
        }

        final Map<String, List<Person>> employeesIndex = getEmployees().stream()
                .flatMap(e -> e.getJobHistory().stream()
                        .limit(1)
                        .map(jhe -> new PersonEmployerPair(e.getPerson(), jhe.getEmployer())))
                .collect(groupingBy(PersonEmployerPair::getEmployer, mapping(PersonEmployerPair::getPerson, toList())));

        assertThat(employeesIndex.entrySet(), containsInAnyOrder(expected.entrySet().toArray()));
    }

    @Getter
    @AllArgsConstructor
    private class TotalExperience {
        private final Person person;
        private final String employer;
        private final int duration;
    }

    @Test
    public void greatestExperiencePerEmployer() {
        final Map<String, Person> expected = new HashMap<>();
        {   // Expected
            final Map<String, Integer> mostExperiencedByEmployer = new HashMap<>();
            for (Employee e : getEmployees()) {
                final Person p = e.getPerson();
                final Map<String, Integer> experience = new HashMap<>();
                for (JobHistoryEntry jhe : e.getJobHistory()) {
                    String emp = jhe.getEmployer();
                    Integer expEmp = experience.get(emp);
                    experience.put(emp, expEmp == null ? jhe.getDuration() : expEmp + jhe.getDuration());
                }
                for (String emp : experience.keySet()) {
                    final Integer mostExperienced = mostExperiencedByEmployer.get(emp);
                    Integer myExp = experience.get(emp);
                    if (mostExperienced == null || mostExperienced < myExp) {
                        expected.put(emp, p);
                        mostExperiencedByEmployer.put(emp, myExp);
                    }
                }
            }
        }

        //noinspection OptionalGetWithoutIsPresent: cannot have empty stream in groupingBy()
        final Map<String, Person> employeesIndex = getEmployees().stream()
                .flatMap(e -> e.getJobHistory().stream()
                        .collect(groupingBy(JobHistoryEntry::getEmployer, summingInt(JobHistoryEntry::getDuration)))
                        .entrySet().stream()
                        .map(empDuration -> new TotalExperience(e.getPerson(), empDuration.getKey(), empDuration.getValue())))
                .collect(groupingBy(TotalExperience::getEmployer,
                        collectingAndThen(
                                maxBy(comparing(TotalExperience::getDuration)),
                                totalExp -> totalExp.get().getPerson())));

        assertEquals(new Person("John", "White", 28), employeesIndex.get("epam"));
        assertThat(employeesIndex.entrySet(), containsInAnyOrder(expected.entrySet().toArray()));
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
