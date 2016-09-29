package part1.exercise;

import data.Employee;
import data.JobHistoryEntry;
import data.Person;
import org.junit.Assert;
import org.junit.Test;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static data.Generator.generateEmployeeList;
import static java.util.stream.Collectors.*;

public class StreamsExercise1 {
    // TODO
    // https://youtu.be/kxgo7Y4cdA8 Сергей Куксенко и Алексей Шипилёв — Через тернии к лямбдам, часть 1
    // https://youtu.be/JRBWBJ6S4aU Сергей Куксенко и Алексей Шипилёв — Через тернии к лямбдам, часть 2

    // TODO
    // https://youtu.be/O8oN4KSZEXE Сергей Куксенко — Stream API, часть 1
    // https://youtu.be/i0Jr2l3jrDA Сергей Куксенко — Stream API, часть 2

    @Test
    public void getAllEpamEmployees() {
        final List<Employee> employees = generateEmployeeList();

        Set<Person> expected = new HashSet<>();
        for (Employee e : employees) {
            for (JobHistoryEntry j : e.getJobHistory()) {
                if (j.getEmployer().equals("epam")) expected.add(e.getPerson());
            }
        }

        Set<Person> result = employees.stream()
                .filter(e -> e.getJobHistory().stream()
                        .anyMatch(j -> j.getEmployer().equals("epam")))
                .map(Employee::getPerson)
                .collect(toSet());

        Assert.assertEquals(expected, result);
    }

    @Test
    public void getEmployeesStartedFromEpam() {
        final List<Employee> employees = generateEmployeeList();

        List<Person> expected = new ArrayList<>();
        for (Employee e : employees) {
            if (e.getJobHistory().get(0).getEmployer().equals("epam")) expected.add(e.getPerson());
        }

        List<Person> result = employees.stream()
                .filter(e -> e.getJobHistory().get(0).getEmployer().equals("epam"))
                .map(e -> e.getPerson())
                .collect(toList());

        Assert.assertEquals(expected, result);
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
                .flatMapToInt(employee -> employee.getJobHistory().stream()
                        .filter(jobHistoryEntry -> jobHistoryEntry.getEmployer().equals("epam"))
                        .mapToInt(JobHistoryEntry::getDuration))
                .sum();

        Assert.assertEquals(expected, result);
    }

}
