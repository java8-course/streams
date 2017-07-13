package part1.exercise;

import data.Employee;
import data.JobHistoryEntry;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Predicate;

import static data.Generator.generateEmployeeList;
import static java.util.stream.Collectors.toList;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

public class StreamsExercise1 {
    // https://youtu.be/kxgo7Y4cdA8 Сергей Куксенко и Алексей Шипилёв — Через тернии к лямбдам, часть 1
    // https://youtu.be/JRBWBJ6S4aU Сергей Куксенко и Алексей Шипилёв — Через тернии к лямбдам, часть 2

    // https://youtu.be/O8oN4KSZEXE Сергей Куксенко — Stream API, часть 1
    // https://youtu.be/i0Jr2l3jrDA Сергей Куксенко — Stream API, часть 2
    private final static String EPAM = "epam";
    private final Predicate<String> isEpam = s -> s.equals(EPAM);

    @Test
    public void getAllEpamEmployees() {

        List<Employee> initialEmployees = generateEmployeeList();
        List<Employee> actual = initialEmployees.stream()
                .filter(employee ->
                        employee.getJobHistory().stream()
                                .map(JobHistoryEntry::getEmployer)
                                .anyMatch(isEpam))
                .collect(toList());

        List<Employee> expected = new ArrayList<>();
        for (Employee employee : initialEmployees) {
            boolean isEpamEmloyee = false;
            for (JobHistoryEntry entry : employee.getJobHistory()) {
                if (isEpam.test(entry.getEmployer())) {
                    isEpamEmloyee = true;
                }
            }
            if (isEpamEmloyee) expected.add(employee);
        }
        assertThat(actual, is(expected));
    }

    @Test
    public void getEmployeesStartedFromEpam() {
        List<Employee> initialList = generateEmployeeList();

        List<Employee> actualList = initialList.stream()
                .filter(employee -> employee.getJobHistory()
                        .stream()
                        .findFirst()
                        .map(JobHistoryEntry::getEmployer)
                        .filter(isEpam)
                        .map(than -> true)
                        .orElse(false))
                .collect(toList());

        List<Employee> expectedList = new ArrayList<>();
        for (Employee employee : initialList) {
            List<JobHistoryEntry> jobHistory = employee.getJobHistory();
            boolean epamIsFirstEmployer = false;
            if (!jobHistory.isEmpty()) {
                String employer = jobHistory.get(0).getEmployer();
                epamIsFirstEmployer = isEpam.test(employer);
            }
            if (epamIsFirstEmployer) {
                expectedList.add(employee);
            }
        }
        assertThat(actualList, is(expectedList));
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

        int actual = employees.stream()
                .map(Employee::getJobHistory)
                .flatMap(Collection::stream)
                .filter(s -> isEpam.test(s.getEmployer()))
                .mapToInt(JobHistoryEntry::getDuration)
                .sum();

        assertEquals(expected, actual);
    }

}
