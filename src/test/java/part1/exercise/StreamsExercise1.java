package part1.exercise;

import data.Employee;
import data.Generator;
import data.JobHistoryEntry;
import data.Person;
import org.junit.Test;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static data.Generator.generateEmployeeList;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.mapping;
import static java.util.stream.Collectors.toList;
import static org.junit.Assert.assertEquals;

public class StreamsExercise1 {
    // https://youtu.be/kxgo7Y4cdA8 Сергей Куксенко и Алексей Шипилёв — Через тернии к лямбдам, часть 1
    // https://youtu.be/JRBWBJ6S4aU Сергей Куксенко и Алексей Шипилёв — Через тернии к лямбдам, часть 2

    // https://youtu.be/O8oN4KSZEXE Сергей Куксенко — Stream API, часть 1
    // https://youtu.be/i0Jr2l3jrDA Сергей Куксенко — Stream API, часть 2

    @Test
    public void getAllEpamEmployees() {
        final List<Employee> employeeList = Generator.generateEmployeeList();

        List<Person> actualPersonList = employeeList.stream()
                .filter(employee -> employee.getJobHistory().stream()
                        .map(JobHistoryEntry::getEmployer)
                        .anyMatch("epam"::equalsIgnoreCase)
                ).map(Employee::getPerson)
                .collect(toList());

        List<Person> expectedPersonList = new ArrayList<>();
        for (Employee employee : employeeList) {
            for (JobHistoryEntry historyEntry : employee.getJobHistory()) {
                if (historyEntry.getEmployer().equalsIgnoreCase("epam")) {
                    expectedPersonList.add(employee.getPerson());
                    break;
                }
            }
        }
        assertEquals(actualPersonList, expectedPersonList);
    }

    @Test
    public void getEmployeesStartedFromEpam() {
        final List<Employee> employeeList = Generator.generateEmployeeList();
        List<Person> epamEmployees = employeeList.stream()
                .filter(e -> e.getJobHistory().stream().limit(1).collect(toList()).get(0).getEmployer().equals("epam"))
                .map(Employee::getPerson)
                .collect(Collectors.toList());
        List<Person> expectedList = new ArrayList<>();

        for (Employee anEmployeeList : employeeList) {
            if (anEmployeeList.getJobHistory().get(0).getEmployer().equalsIgnoreCase("epam")) {

                expectedList.add(anEmployeeList.getPerson());
            }
        }
        assertEquals(expectedList, epamEmployees);
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
                .flatMap(employee -> employee.getJobHistory().stream())
                .filter(j -> j.getEmployer().equals("epam"))
                .mapToInt(JobHistoryEntry::getDuration)
                .sum();
        assertEquals(expected, result);
    }

}
