package part1.exercise;

import data.Employee;
import data.JobHistoryEntry;
import data.Person;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static data.Generator.generateEmployeeList;
import static org.junit.Assert.assertEquals;

public class StreamsExercise1 {
    // https://youtu.be/kxgo7Y4cdA8 Сергей Куксенко и Алексей Шипилёв — Через тернии к лямбдам, часть 1
    // https://youtu.be/JRBWBJ6S4aU Сергей Куксенко и Алексей Шипилёв — Через тернии к лямбдам, часть 2

    // https://youtu.be/O8oN4KSZEXE Сергей Куксенко — Stream API, часть 1
    // https://youtu.be/i0Jr2l3jrDA Сергей Куксенко — Stream API, часть 2

    @Test
    public void getAllEpamEmployees() {
        final List<Employee> employees = generateEmployeeList();

        List<Person> expected = new ArrayList<>();
        for (final Employee employee : employees)
            for (final JobHistoryEntry entry : employee.getJobHistory())
                if (entry.getEmployer().equalsIgnoreCase("epam")) {
                    expected.add(employee.getPerson());
                    break;
                }

        List<Person> epamEmployees = employees.stream()
                .filter(employee -> employee.getJobHistory().stream()
                        .anyMatch(entry -> entry.getEmployer().equalsIgnoreCase("epam")))
                .map(Employee::getPerson)
                .collect(Collectors.toList());

        assertEquals(expected, epamEmployees);
    }

    @Test
    public void getEmployeesStartedFromEpam() {
        final List<Employee> employees = generateEmployeeList();

        List<Person> expected = new ArrayList<>();
        for (final Employee employee : employees)
            if (!employee.getJobHistory().isEmpty() &&
                    employee.getJobHistory().get(0).getEmployer().equalsIgnoreCase("epam"))
                expected.add(employee.getPerson());

        List<Person> employeesStartedFromEpam = employees.stream()
                .filter(employee -> !employee.getJobHistory().isEmpty() &&
                        employee.getJobHistory().get(0).getEmployer().equalsIgnoreCase("epam"))
                .map(Employee::getPerson)
                .collect(Collectors.toList());

        assertEquals(expected, employeesStartedFromEpam);
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

        int result = employees.stream()
                .flatMap(employee -> employee.getJobHistory().stream())
                .filter(entry -> entry.getEmployer().equalsIgnoreCase("epam"))
                .mapToInt(JobHistoryEntry::getDuration)
                .sum();

        assertEquals(expected, result);
    }

}
