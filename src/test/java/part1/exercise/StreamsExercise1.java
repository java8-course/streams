package part1.exercise;

import data.Employee;
import data.JobHistoryEntry;
import data.Person;
import org.junit.Before;
import org.junit.Test;

import java.util.List;
import java.util.stream.Collectors;

import static data.Generator.generateEmployeeList;
import static org.junit.Assert.assertEquals;

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
                        .collect(Collectors.toList());

      // TODO: test
    }

    @Test
    public void getEmployeesStartedFromEpam() {
        List<Person> epamEmployees =
                employees.stream()
                        .filter((employee ->
                                employee.getJobHistory().get(0).getEmployer().equals("epam")))
                        .map(Employee::getPerson)
                        .collect(Collectors.toList());

        // TODO: test

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
                .reduce(0, (left, right) -> left + right);
        assertEquals(expected, result);
    }

}
