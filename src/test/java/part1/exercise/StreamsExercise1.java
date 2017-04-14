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
import static junit.framework.TestCase.assertEquals;

public class StreamsExercise1 {
    // https://youtu.be/kxgo7Y4cdA8 Сергей Куксенко и Алексей Шипилёв — Через тернии к лямбдам, часть 1
    // https://youtu.be/JRBWBJ6S4aU Сергей Куксенко и Алексей Шипилёв — Через тернии к лямбдам, часть 2

    // https://youtu.be/O8oN4KSZEXE Сергей Куксенко — Stream API, часть 1
    // https://youtu.be/i0Jr2l3jrDA Сергей Куксенко — Stream API, часть 2

    @Test
    public void getAllEpamEmployees() {
        final List<Employee> employees = generateEmployeeList();

        List<Person> expected = new ArrayList<>();

        for (Employee e : employees) {
            for (JobHistoryEntry j : e.getJobHistory()) {
                if (j.getEmployer().equals("epam")) {
                    expected.add(e.getPerson());
                    break;
                }
            }
        }

        List<Person> result = employees.stream()
                .filter(emp -> emp.getJobHistory().stream()
                        .map(JobHistoryEntry::getEmployer)
                        .anyMatch(e -> e.equals("epam")))
                .map(Employee::getPerson)
                .collect(toList());

        assertEquals(expected, result);
    }

    @Test
    public void getEmployeesStartedFromEpam() {
        final List<Employee> employees = generateEmployeeList();

        List<Person> expected = new ArrayList<>();

        for (Employee e : employees) {

            List<JobHistoryEntry> jobHistory = e.getJobHistory();
            if (!jobHistory.isEmpty() && jobHistory.get(0).getEmployer().equals("epam"))
                expected.add(e.getPerson());
        }

        List<Person> result = employees.stream()
                .filter(emp -> emp.getJobHistory().get(0).getEmployer().equals("epam"))
                .map(Employee::getPerson)
                .collect(toList());

        assertEquals(expected, result);
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
                .flatMap(emp -> emp.getJobHistory().stream())
                .filter(job -> job.getEmployer().equals("epam"))
                .mapToInt(JobHistoryEntry::getDuration)
                .sum();
        assertEquals(expected, result);
    }

}
