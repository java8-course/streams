package part1.exercise;

import data.Employee;
import data.JobHistoryEntry;
import data.Person;
import org.junit.Test;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static data.Generator.generateEmployeeList;
import static org.junit.Assert.assertEquals;

public class StreamsExercise1 {
    // https://youtu.be/kxgo7Y4cdA8 Сергей Куксенко и Алексей Шипилёв — Через тернии к лямбдам, часть 1
    // https://youtu.be/JRBWBJ6S4aU Сергей Куксенко и Алексей Шипилёв — Через тернии к лямбдам, часть 2

    // https://youtu.be/O8oN4KSZEXE Сергей Куксенко — Stream API, часть 1
    // https://youtu.be/i0Jr2l3jrDA Сергей Куксенко — Stream API, часть 2

    @Test
    public void getAllEpamEmployees() {
        List<Employee> employees = generateEmployeeList();
        Set<Person> expected = new HashSet<>();

        for (Employee employee : employees) {
            for (JobHistoryEntry entry : employee.getJobHistory()) {
                if (entry.getEmployer().equals("epam")) {
                    if (!expected.contains(employee.getPerson()))
                        expected.add(employee.getPerson());
                }
            }
        }
        Set<Person> epamEmployees = employees
                .stream()
                .flatMap(StreamsExercise1::employeeToPairs)
                .filter(t -> t.getEmployer().equals("epam"))
                .map(PersonEmployerPair::getPerson)
                .distinct()
                .collect(Collectors.toSet());
        assertEquals(expected, epamEmployees);

    }

    @Test
    public void getEmployeesStartedFromEpam() {
        List<Employee> employees = generateEmployeeList();
        List<Person> expected = new ArrayList<>();

        for (Employee employee : employees) {
            if (employee.getJobHistory().get(0).getEmployer().equals("epam")) {
                expected.add(employee.getPerson());
            }
        }

        List<Person> epamEmployees = employees.stream()
                .filter(t -> t.getJobHistory().get(0).getEmployer().equals("epam"))
                .map(Employee::getPerson)
                .collect(Collectors.toList());

        final List<Person> epam = employees.stream()
                .filter(t -> t.getJobHistory().stream()
                        .limit(1)
                        .findAny()
                        .orElse(new JobHistoryEntry(-1, "Default", "Default"))
                        .getEmployer().equals("epam"))
                .map(Employee::getPerson)
                .collect(Collectors.toList());

        assertEquals(expected, epam);
    }

    @Test
    public void sumEpamDurations() {
        final List<Employee> employees = generateEmployeeList();

        int expected = 0;

        for (Employee e : employees) {
            for (JobHistoryEntry j : e.getJobHistory()) {
                if (j.getEmployer().equals("epam")) {
                    expected += j.getDuration();
                }
            }
        }

        final int result = employees.stream()
                .map(Employee::getJobHistory)
                .flatMap(Collection::stream)
                .filter(t -> t.getEmployer().equals("epam"))
                .mapToInt(JobHistoryEntry::getDuration)
                .sum();

        assertEquals(expected, result);
    }

    private static Stream<PersonEmployerPair> employeeToPairs(Employee employee) {
        return employee.getJobHistory().stream()
                .map(JobHistoryEntry::getEmployer)
                .map(p -> new PersonEmployerPair(employee.getPerson(), p));
    }

    public static class PersonEmployerPair {
        private final Person person;
        private final String employer;

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

}
