package part1.exercise;

import data.Employee;
import data.JobHistoryEntry;
import data.Person;
import org.junit.Test;
import java.util.*;
import static data.Generator.generateEmployeeList;
import static java.util.stream.Collectors.toList;
import static org.junit.Assert.assertEquals;

public class StreamsExercise1 {
    // https://youtu.be/kxgo7Y4cdA8 Сергей Куксенко и Алексей Шипилёв — Через тернии к лямбдам, часть 1
    // https://youtu.be/JRBWBJ6S4aU Сергей Куксенко и Алексей Шипилёв — Через тернии к лямбдам, часть 2

    // https://youtu.be/O8oN4KSZEXE Сергей Куксенко — Stream API, часть 1
    // https://youtu.be/i0Jr2l3jrDA Сергей Куксенко — Stream API, часть 2

    public static boolean isEpamWorker(Employee employee) {
        return employee.getJobHistory().stream()
                .map(JobHistoryEntry::getEmployer)
                .anyMatch("epam"::equals);
    }

    @Test
    public void getAllEpamEmployees() {
        List<Employee> epamEmployees = generateEmployeeList();
        List<Employee> actualEpamEmployees = epamEmployees.stream()
                .filter(StreamsExercise1::isEpamWorker)
                .collect(toList()); // all persons with experience in epam

        List<Employee> expectedEpamEmployees = new ArrayList<>();
        for (Employee e: epamEmployees) {
            for (JobHistoryEntry historyEntry: e.getJobHistory()) {
                if (historyEntry.getEmployer().equals("epam")) {
                    expectedEpamEmployees.add(e);
                    epamEmployees.stream().collect(toList());
                }
            }
        }
        assertEquals(expectedEpamEmployees, actualEpamEmployees);
    }

    public static boolean isWorkerStartedFromEpam(Employee employee) {
        return employee.getJobHistory().stream()
                .limit(1)
                .map(JobHistoryEntry::getEmployer)
                .anyMatch("epam"::equals);
    }

    @Test
    public void getEmployeesStartedFromEpam() {
        List<Employee> epamEmployees = generateEmployeeList();

        List<Person> actualEpamEmployees = epamEmployees.stream()
                .filter(StreamsExercise1::isWorkerStartedFromEpam)
                .map(Employee::getPerson)
                .collect(toList()); // all persons with first experience in epam

        List<Person> expectedEpamEmployees = new ArrayList<>();
        for (Employee e: epamEmployees) {
            if (e.getJobHistory().get(0).getEmployer().equals("epam")) {
                expectedEpamEmployees.add(e.getPerson());
                epamEmployees.stream().collect(toList());
            }
        }
        assertEquals(expectedEpamEmployees, actualEpamEmployees);
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

        final Employee[] employeesArray = employees.toArray(new Employee[0]);
        int result = Arrays.stream(employeesArray)
                .map(Employee::getJobHistory)
                .flatMap(Collection::stream)
                .filter(e -> e.getEmployer().equals("epam"))
                .mapToInt(JobHistoryEntry::getDuration)
                .sum();
        assertEquals(expected, result);
    }
}