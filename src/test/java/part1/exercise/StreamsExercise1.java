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
import static java.util.stream.Collectors.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class StreamsExercise1 {
    // https://youtu.be/kxgo7Y4cdA8 Сергей Куксенко и Алексей Шипилёв — Через тернии к лямбдам, часть 1
    // https://youtu.be/JRBWBJ6S4aU Сергей Куксенко и Алексей Шипилёв — Через тернии к лямбдам, часть 2

    // https://youtu.be/O8oN4KSZEXE Сергей Куксенко — Stream API, часть 1
    // https://youtu.be/i0Jr2l3jrDA Сергей Куксенко — Stream API, часть 2

    @Test
    public void getAllEpamEmployees() {
        // TODO all persons with experience in epam
        List<Employee> employeeList = Generator.generateEmployeeList();
        List<Person> personsWithExperienceInEpam = employeeList.stream()
                .filter(e -> e.getJobHistory().stream()
                        .anyMatch(j -> j.getEmployer().equals("epam")))
                .map(Employee::getPerson)
                .collect(toList());

        List<Person> expected = new ArrayList<>();
        for (Employee employee : employeeList) {
            inner:
            for (JobHistoryEntry jobHistoryEntry : employee.getJobHistory()) {
                if (jobHistoryEntry.getEmployer().equals("epam")) {
                    expected.add(employee.getPerson());
                    break inner;
                }
            }
        }

        assertEquals(personsWithExperienceInEpam.size(), expected.size());
        assertTrue(personsWithExperienceInEpam.containsAll(expected));
    }

    @Test
    public void getEmployeesStartedFromEpam() {
        // TODO all persons with first experience in epam
        List<Employee> employeeList = Generator.generateEmployeeList();
        List<Person> personsWithFirstExperienceInEpam = employeeList.stream()
                .filter(
                        e -> !e.getJobHistory().isEmpty()
                                && e.getJobHistory().get(0).getEmployer().equals("epam"))
                .map(Employee::getPerson)
                .collect(toList());


        List<Person> expected = new ArrayList<>();
        for (Employee employee : employeeList) {
            List<JobHistoryEntry> jobHistory = employee.getJobHistory();
            if (!jobHistory.isEmpty() && jobHistory.get(0).getEmployer().equals("epam"))
                expected.add(employee.getPerson());
        }

        assertEquals(personsWithFirstExperienceInEpam.size(), expected.size());
        assertTrue(personsWithFirstExperienceInEpam.containsAll(expected));
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

        // TODO
        int result = employees.stream()
                .flatMap(e -> e.getJobHistory().stream())
                .filter(j -> j.getEmployer().equals("epam"))
                .mapToInt(j -> j.getDuration())
                .sum(); //Or .reduce(0, (a, b) -> a + b);

        assertEquals(expected, result);
    }

}
