package part1.exercise;

import data.Employee;
import data.JobHistoryEntry;
import data.Person;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static data.Generator.generateEmployeeList;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

public class StreamsExercise1 {
    // https://youtu.be/kxgo7Y4cdA8 Сергей Куксенко и Алексей Шипилёв — Через тернии к лямбдам, часть 1
    // https://youtu.be/JRBWBJ6S4aU Сергей Куксенко и Алексей Шипилёв — Через тернии к лямбдам, часть 2

    // https://youtu.be/O8oN4KSZEXE Сергей Куксенко — Stream API, часть 1
    // https://youtu.be/i0Jr2l3jrDA Сергей Куксенко — Stream API, часть 2

    private List<Employee> employees;

    @Before
    public void generateEmployees() {
        employees = generateEmployeeList();
    }

    @Test
    public void getAllEpamEmployees() {
        List<Person> epamEmployees =
                employees.stream()
                        .filter(employee -> employee
                                .getJobHistory()
                                .stream()
                                .anyMatch(jobHistoryEntry -> jobHistoryEntry.getEmployer().equals("epam")))
                        .map(Employee::getPerson)
                        .distinct()
                        .collect(Collectors.toList());

        List<Person> expected = new ArrayList<>();
        for (Employee e : employees) {
            for (JobHistoryEntry jhe : e.getJobHistory())
                if (jhe.getEmployer().equals("epam")) {
                    final Person candidate = e.getPerson();
                    if (!expected.contains(candidate)) expected.add(candidate);
                }
        }

        assertThat(epamEmployees, containsInAnyOrder(expected.toArray()));
    }

    @Test
    public void getEmployeesStartedFromEpam() {
        List<Person> epamEmployees =
                employees.stream()
                        .filter(employee ->
                                employee.getJobHistory().stream()
                                        .findFirst()
                                        .map(jhe -> "epam".equals(jhe.getEmployer()))
                                        .orElse(false))
                        .map(Employee::getPerson)
                        .collect(Collectors.toList());

        List<Person> expected = new ArrayList<>();
        for (Employee e : employees)
            if (e.getJobHistory().get(0).getEmployer().equals("epam"))
                expected.add(e.getPerson());

        assertThat(epamEmployees, containsInAnyOrder(expected.toArray()));
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
                .filter(jhe -> jhe.getEmployer().equals("epam"))
                .mapToInt(JobHistoryEntry::getDuration)
                .sum();     //.reduce(0, (left, right) -> left + right);
        assertEquals(expected, result);
    }

}
