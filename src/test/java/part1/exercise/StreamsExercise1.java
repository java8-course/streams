package part1.exercise;

import data.Employee;
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
        List<Employee> epamList = generateEmployeeList();// all persons with experience in epam
        List<Person> epamEmployees = epamList.stream()
                .filter(e -> e.getJobHistory()
                        .stream()
                        .map(JobHistoryEntry::getEmployer)
                        .anyMatch(emp -> emp.equalsIgnoreCase("epam")))
                .map(Employee::getPerson)
                .collect(toList());
        List<Person> expectedList = new ArrayList<>();
        epamList.forEach(e -> {
            for (JobHistoryEntry job : e.getJobHistory()) {
                if (job.getEmployer().equals("epam")) {
                    expectedList.add(e.getPerson());
                    break;
                }
            }
        });
        assertEquals(epamEmployees, expectedList);
    }

    @Test
    public void getEmployeesStartedFromEpam() {
        List<Employee> emp = generateEmployeeList();// all persons with first experience in epam
        List<Person> epamEmployees = emp.stream()
                .filter(e -> e.getJobHistory().get(0).getEmployer().equalsIgnoreCase("epam"))
                .map(Employee::getPerson)
                .collect(toList());
        List<Person> expectedList = new ArrayList<>();
        emp.forEach(e -> {
            if (e.getJobHistory().get(0).getEmployer().equalsIgnoreCase("epam")) {
                expectedList.add(e.getPerson());
            }
        });
        assertEquals(epamEmployees, expectedList);
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
                .filter(emp -> emp.getEmployer().equalsIgnoreCase("epam"))
                .mapToInt(JobHistoryEntry::getDuration)
                .sum();
        assertEquals(expected, result);
    }

}
