package part1.exercise;

import data.Employee;
import data.JobHistoryEntry;
import data.Person;
import junit.framework.Assert;
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

public class StreamsExercise1 {
    // https://youtu.be/kxgo7Y4cdA8 Сергей Куксенко и Алексей Шипилёв — Через тернии к лямбдам, часть 1
    // https://youtu.be/JRBWBJ6S4aU Сергей Куксенко и Алексей Шипилёв — Через тернии к лямбдам, часть 2

    // https://youtu.be/O8oN4KSZEXE Сергей Куксенко — Stream API, часть 1
    // https://youtu.be/i0Jr2l3jrDA Сергей Куксенко — Stream API, часть 2

    @Test
    public void getAllEpamEmployees() {
        List<Person> epamEmployees;// TODO all persons with experience in epam
        List<Employee> employees = generateEmployeeList();
        List<Person> expectedResult = new ArrayList<>();

        for (Employee e: employees) {
            for (JobHistoryEntry j: e.getJobHistory()) {
                if (j.getEmployer().equals("epam")) {
                    expectedResult.add(e.getPerson());
                    break;
                }
            }
        }

        epamEmployees = employees
                .stream()
                .filter(StreamsExercise1::isEpamEr)
                .map(Employee::getPerson)
                .collect(toList());

        Assert.assertEquals(expectedResult, epamEmployees);
    }

    private static boolean isEpamEr(Employee e) {
        return e.getJobHistory()
            .stream()
            .anyMatch(j -> "epam".equals(j.getEmployer()));
    }

    public static boolean isStartedFromEpam(Employee e) {
        return e.getJobHistory().stream()
                .limit(1)
                .anyMatch(j -> "epam".equals(j.getEmployer()));
    }

    @Test
    public void getEmployeesStartedFromEpam() {
        List<Person> epamEmployees;// TODO all persons with first experience in epam
        List<Employee> employees = generateEmployeeList();
        List<Person> expectedResult = new ArrayList<>();

        for (Employee e: employees) {
            if (e.getJobHistory().get(0).getEmployer().equals("epam")) {
                expectedResult.add(e.getPerson());
            }
        }
        epamEmployees = employees.stream()
                .filter(StreamsExercise1::isStartedFromEpam)
                .map(Employee::getPerson)
                .collect(toList());

        Assert.assertEquals(epamEmployees, expectedResult);
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
                .filter(e -> e.getEmployer().equals("epam"))
                .mapToInt(JobHistoryEntry::getDuration)
                .sum();

        Assert.assertEquals(expected, result);
    }

}
