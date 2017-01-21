package part1.exercise;

import data.Employee;
import data.Generator;
import data.JobHistoryEntry;
import data.Person;
import org.junit.Test;

import java.util.*;

import static data.Generator.generateEmployeeList;
import static java.util.stream.Collectors.toList;
import static junit.framework.TestCase.assertEquals;
import static org.junit.Assert.assertTrue;

public class StreamsExercise1 {
    // https://youtu.be/kxgo7Y4cdA8 Сергей Куксенко и Алексей Шипилёв — Через тернии к лямбдам, часть 1
    // https://youtu.be/JRBWBJ6S4aU Сергей Куксенко и Алексей Шипилёв — Через тернии к лямбдам, часть 2

    // https://youtu.be/O8oN4KSZEXE Сергей Куксенко — Stream API, часть 1
    // https://youtu.be/i0Jr2l3jrDA Сергей Куксенко — Stream API, часть 2



    @Test
    public void getAllEpamEmployees() {

        final List<Employee> employeeList = Generator.generateEmployeeList();

        final Employee epamEmployee = new Employee(
                new Person("Bob", "Doe", 27),
                Arrays.asList(
                        new JobHistoryEntry(4, "QA", "yandex"),
                        new JobHistoryEntry(2, "QA", "epam"),
                        new JobHistoryEntry(2, "dev", "abc")
                ));

        final Employee notEpamEmployee = new Employee(
                new Person("John", "Smith", 28),
                Arrays.asList(
                        new JobHistoryEntry(4, "QA", "yandex"),
                        new JobHistoryEntry(2, "dev", "google")
                ));


        employeeList.add(epamEmployee);
        employeeList.add(notEpamEmployee);

        List<Person> epamEmployees = employeeList.stream()
                .filter(p -> p.getJobHistory().stream()
                        .map(JobHistoryEntry::getEmployer)
                        .anyMatch("epam"::equalsIgnoreCase))
                .map(Employee::getPerson)
                .collect(toList());

        assertTrue(epamEmployees.contains(epamEmployee.getPerson()));
        assertTrue(!epamEmployees.contains(notEpamEmployee.getPerson()));

    }

    @Test
    public void getEmployeesStartedFromEpam() {

        final List<Employee> employeeList = Generator.generateEmployeeList();

        final Employee employeeStartedFromEpam = new Employee(
                new Person("Bob", "Doe", 27),
                Arrays.asList(
                        new JobHistoryEntry(2, "QA", "epam"),
                        new JobHistoryEntry(4, "QA", "yandex"),
                        new JobHistoryEntry(2, "dev", "abc")
                ));

        final Employee employeeNotStartedFromEpam = new Employee(
                new Person("John", "Smith", 28),
                Arrays.asList(
                        new JobHistoryEntry(4, "QA", "yandex"),
                        new JobHistoryEntry(2, "dev", "google"),
                        new JobHistoryEntry(2, "QA", "epam"),
                        new JobHistoryEntry(2, "dev", "abc")
                ));


        employeeList.add(employeeStartedFromEpam);
        employeeList.add(employeeNotStartedFromEpam);

        List<Person> employeesStartedFromEpam = employeeList.stream()
                .filter(e -> e.getJobHistory().stream()
                        .map(JobHistoryEntry::getEmployer)
                        .limit(1)
                        .anyMatch("epam"::equalsIgnoreCase))
                .map(Employee::getPerson)
                .collect(toList());

        assertTrue(employeesStartedFromEpam.contains(employeeStartedFromEpam.getPerson()));
        assertTrue(!employeesStartedFromEpam.contains(employeeNotStartedFromEpam.getPerson()));

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
                .flatMap(e -> e.getJobHistory().stream())
                .filter(j->"epam".equalsIgnoreCase(j.getEmployer()))
                .mapToInt(JobHistoryEntry::getDuration)
                .sum();

        assertEquals(expected, result);
    }

}
