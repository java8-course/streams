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
import static junit.framework.TestCase.assertEquals;

public class StreamsExercise1 {
    // https://youtu.be/kxgo7Y4cdA8 Сергей Куксенко и Алексей Шипилёв — Через тернии к лямбдам, часть 1
    // https://youtu.be/JRBWBJ6S4aU Сергей Куксенко и Алексей Шипилёв — Через тернии к лямбдам, часть 2

    // https://youtu.be/O8oN4KSZEXE Сергей Куксенко — Stream API, часть 1
    // https://youtu.be/i0Jr2l3jrDA Сергей Куксенко — Stream API, часть 2

    @Test
    public void getAllEpamEmployees() {
        List<Person> epam = Generator.generateEmployeeList()
                .stream()
                .filter(e -> e.getJobHistory().stream().anyMatch(entry -> entry.getEmployer().equals("epam")))
                .map(Employee::getPerson)
                .collect(Collectors.toList());
        // TODO all persons with experience in epam
    }

    @Test
    public void getEmployeesStartedFromEpam() {
        List<Person> epamEmployees = Generator.generateEmployeeList().stream()
                .filter(e -> e.getJobHistory().stream().findFirst()
                        .filter(entry -> entry.getEmployer().equals("epam"))
                        .isPresent())
                .map(Employee::getPerson)
                .collect(Collectors.toList());
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
                .filter(s -> s.getEmployer().equals("epam"))
                .mapToInt(JobHistoryEntry::getDuration)
                .sum();
        // int result = ???
        assertEquals(expected, result);
    }

}

