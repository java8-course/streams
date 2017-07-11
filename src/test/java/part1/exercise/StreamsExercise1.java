package part1.exercise;

import data.Employee;
import data.JobHistoryEntry;
import org.junit.Test;

import java.util.Collection;
import java.util.List;

import static data.Generator.generateEmployeeList;
import static java.util.stream.Collectors.toList;
import static org.junit.Assert.assertEquals;

public class StreamsExercise1 {
    // https://youtu.be/kxgo7Y4cdA8 Сергей Куксенко и Алексей Шипилёв — Через тернии к лямбдам, часть 1
    // https://youtu.be/JRBWBJ6S4aU Сергей Куксенко и Алексей Шипилёв — Через тернии к лямбдам, часть 2

    // https://youtu.be/O8oN4KSZEXE Сергей Куксенко — Stream API, часть 1
    // https://youtu.be/i0Jr2l3jrDA Сергей Куксенко — Stream API, часть 2

    @Test
    public void getAllEpamEmployees() {
        List<Employee> employees = generateEmployeeList(); // all persons with experience in epam

        List<Employee> epamEmployees = employees.stream()
                .filter(this::workedInEpam)
                .collect(toList());
    }

    private boolean workedInEpam(Employee employee) {
        return employee.getJobHistory().stream()
                .map(JobHistoryEntry::getEmployer)
                .anyMatch("epam"::equals);
    }

    @Test
    public void getEmployeesStartedFromEpam() {
        List<Employee> employees = generateEmployeeList();// all persons with first experience in epam

        List<Employee> epamEmployees = employees.stream()
                .filter(this::workedFirstlyInEpam)
                .collect(toList());
    }

    private boolean workedFirstlyInEpam(Employee employee) {
        return employee.getJobHistory().stream()
                .findFirst()
                .filter("epam"::equals)
                .isPresent();
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
                 .map(Employee::getJobHistory)
                 .flatMap(Collection::stream)
                 .filter(this::epamJobHistoryEntry)
                 .mapToInt(JobHistoryEntry::getDuration)
                 .sum();

         assertEquals(expected, result);
    }

    private boolean epamJobHistoryEntry(JobHistoryEntry jobHistoryEntry) {
        return jobHistoryEntry.getEmployer().equals("epam");
    }

}
