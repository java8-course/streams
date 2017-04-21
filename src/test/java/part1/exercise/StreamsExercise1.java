package part1.exercise;

import data.Employee;
import data.JobHistoryEntry;
import data.Person;
import org.junit.Assert;
import org.junit.Test;

import javax.swing.*;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static data.Generator.generateEmployeeList;
import static java.util.stream.Collectors.groupingBy;
import static org.junit.Assert.assertEquals;

public class StreamsExercise1 {
    // https://youtu.be/kxgo7Y4cdA8 Сергей Куксенко и Алексей Шипилёв — Через тернии к лямбдам, часть 1
    // https://youtu.be/JRBWBJ6S4aU Сергей Куксенко и Алексей Шипилёв — Через тернии к лямбдам, часть 2

    // https://youtu.be/O8oN4KSZEXE Сергей Куксенко — Stream API, часть 1
    // https://youtu.be/i0Jr2l3jrDA Сергей Куксенко — Stream API, часть 2

    private static class PersonEmployer {
        private final Person person;
        private final String employer;

        public PersonEmployer(Person person, String employer) {
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

    private static Stream<PersonEmployer> employeeToPersonEmployer (Employee employee) {
        return employee.getJobHistory().stream()
                .map(t -> t.getEmployer())
                .map(e -> new PersonEmployer(employee.getPerson(), e));
    }

    @Test
    public void getAllEpamEmployees() {
        List<Employee> someEmployees = generateEmployeeList();

        final List<Person> epamEmployees = someEmployees.stream()
                .flatMap(e -> employeeToPersonEmployer(e))
                .filter(t -> t.getEmployer().equals("epam"))
                .map(p -> p.getPerson())
                .collect(Collectors.toList());

        List<Person> expected = new ArrayList<>();

        for (Employee employee : someEmployees) {
            for(JobHistoryEntry j : employee.getJobHistory()) {
                if (j.getEmployer().equals("epam")) {
                    expected.add(employee.getPerson());
                }
            }
        }

        assertEquals(epamEmployees, expected);
    }

    @Test
    public void getEmployeesStartedFromEpam() {
        List<Employee> someEmployees = generateEmployeeList();

        final List<Person> epamEmployees =
                someEmployees.stream()
                .flatMap(e -> employeeToPersonEmployer(e))
                .filter(t -> t.getEmployer().equals("epam"))
                .limit(1)
                .map(p -> p.getPerson())
                .collect(Collectors.toList());

        List<Person> expected = new ArrayList<>();

        for (Employee employee : someEmployees) {
            for(JobHistoryEntry j : employee.getJobHistory()) {
                if (j.getEmployer().equals("epam")) {
                    expected.add(employee.getPerson());
                    break;
                }
            }
        }

        assertEquals(epamEmployees, expected);
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

        // TODO
        throw new UnsupportedOperationException();

        // int result = ???
        // assertEquals(expected, result);
    }

}
