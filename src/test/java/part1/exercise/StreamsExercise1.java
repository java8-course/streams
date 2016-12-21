package part1.exercise;

import data.Employee;
import data.JobHistoryEntry;
import data.Person;
import org.junit.Assert;
import org.junit.Test;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static data.Generator.generateEmployeeList;
import static data.Generator.generatePerson;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.mapping;
import static java.util.stream.Collectors.toList;

public class StreamsExercise1 {
    // https://youtu.be/kxgo7Y4cdA8 Сергей Куксенко и Алексей Шипилёв — Через тернии к лямбдам, часть 1
    // https://youtu.be/JRBWBJ6S4aU Сергей Куксенко и Алексей Шипилёв — Через тернии к лямбдам, часть 2

    // https://youtu.be/O8oN4KSZEXE Сергей Куксенко — Stream API, часть 1
    // https://youtu.be/i0Jr2l3jrDA Сергей Куксенко — Stream API, часть 2


    private static boolean isEpamEmployee(Employee e) {
        return e.getJobHistory()
                .stream()
                .map(JobHistoryEntry::getEmployer)
                .anyMatch("epam"::equals);
    }

    @Test
    public void getAllEpamEmployees() {
        List<Employee> epamEmployees = generateEmployeeList();
        List<Person> expected = new ArrayList<Person>();

        for (Employee e: epamEmployees) {
            for (JobHistoryEntry j: e.getJobHistory()) {
                if (j.getEmployer().equals("epam")) {
                    expected.add(e.getPerson());
                    break;
                }
            }
        }

        List<Person> actual = epamEmployees
                .stream()
                .filter(StreamsExercise1::isEpamEmployee)
                .map(Employee::getPerson)
                .collect(toList());

        Assert.assertEquals(actual, expected);

    }

    private static boolean isStartFromEpam(Employee e) {
        return e.getJobHistory()
                .stream()
                .limit(1)
                .map(JobHistoryEntry::getEmployer)
                .anyMatch("epam"::equals);
    }

    @Test
    public void getEmployeesStartedFromEpam() {
        List<Employee> epamEmployees = generateEmployeeList();
        List<Person> expected = new ArrayList<Person>();

        for (Employee e: epamEmployees) {
            if (e.getJobHistory().get(0).getEmployer().equals("epam")) {
                expected.add(e.getPerson());
            }
        }

        List<Person> actual = epamEmployees
                        .stream()
                        .filter(StreamsExercise1::isStartFromEpam)
                        .map(Employee::getPerson)
                        .collect(toList());

        Assert.assertEquals(actual, expected);

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

        int actual = employees
                .stream()
                .flatMap(e -> e.getJobHistory().stream())
                .filter(j -> j.getEmployer().equals("epam"))
                .mapToInt(JobHistoryEntry::getDuration)
                .sum();

        Assert.assertEquals(expected, actual);
    }

}
