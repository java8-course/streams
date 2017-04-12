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
        List<Employee> employees = Generator.generateEmployeeList();
        List<Person> epamEmployees = employees.stream()
                .filter(
                        e -> e.getJobHistory().stream()
                                .anyMatch(j -> j.getEmployer().equals("epam") && j.getDuration() > 0)
                )
                .map(Employee::getPerson)
                .collect(toList());


        List<Person> expected = new ArrayList<>();
        employees.forEach(
                e -> {
                    List<JobHistoryEntry> jobHistory = e.getJobHistory();
                    for (JobHistoryEntry jobHistoryEntry: jobHistory) {
                        if (jobHistoryEntry.getEmployer().equals("epam")) {
                            expected.add(e.getPerson());
                            break;
                        }
                    }
                }
        );

        assertEquals(expected, epamEmployees);
    }

    @Test
    public void getEmployeesStartedFromEpam() {
        List<Employee> employees = Generator.generateEmployeeList();
        List<Person> epamEmployees = employees.stream()
                .filter(
                        e -> !e.getJobHistory().isEmpty() && e.getJobHistory().get(0).getEmployer().equals("epam")
                )
                .map(Employee::getPerson)
                .collect(toList());

        List<Person> expected = new ArrayList<>();
        employees.forEach(
                e -> {
                    List<JobHistoryEntry> jobHistory = e.getJobHistory();
                    if (!jobHistory.isEmpty() && e.getJobHistory().get(0).getEmployer().equals("epam")) {
                        expected.add(e.getPerson());
                    }
                }
        );

        assertEquals(expected, epamEmployees);
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
                .flatMap(e -> e.getJobHistory().stream().filter(j -> j.getEmployer().equals("epam")))
                .mapToInt(JobHistoryEntry::getDuration)
                .sum();

        assertEquals(expected, result);
    }

}
