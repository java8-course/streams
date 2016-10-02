package part1.exercise;

import data.Employee;
import data.Generator;
import data.JobHistoryEntry;
import data.Person;
import org.junit.Before;
import org.junit.Test;

import java.util.*;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.groupingBy;

public class StreamsExercise1 {
    // https://youtu.be/kxgo7Y4cdA8 Сергей Куксенко и Алексей Шипилёв — Через тернии к лямбдам, часть 1
    // https://youtu.be/JRBWBJ6S4aU Сергей Куксенко и Алексей Шипилёв — Через тернии к лямбдам, часть 2

    // https://youtu.be/O8oN4KSZEXE Сергей Куксенко — Stream API, часть 1
    // https://youtu.be/i0Jr2l3jrDA Сергей Куксенко — Stream API, часть 2

    private static List<Employee> employees;
    @Before
    public static List<Employee> generateEmployeeList() {
        employees = Generator.generateEmployeeList();
        return employees;
    }

    @Test
    public void getAllEpamEmployees() {
        List<Person> epamEmployees = employees.stream()
                .filter(e -> e.getJobHistory().stream()
                        .map(JobHistoryEntry::getEmployer)
                        .anyMatch(job -> job.equals("epam")))
                .map(Employee::getPerson)
                .collect(Collectors.toList());// TODO all persons with experience in epam
    }

    @Test
    public void getEmployeesStartedFromEpam() {
        List<Person> epamEmployees = employees.stream()
                .filter(e -> e.getJobHistory().stream()
                        .map(JobHistoryEntry::getEmployer)
                        .limit(1)
                        .anyMatch(job -> job.equals("epam")))
                .map(Employee::getPerson)
                .collect(Collectors.toList());// TODO all persons with first experience in epam
    }

    @Test
    public void sumEpamDurations() {
        final List<Employee> employees = generateEmployeeList();

        int expected = 0;   //Why 0?

        for (Employee e : employees) {
            for (JobHistoryEntry j : e.getJobHistory()) {
                if (j.getEmployer().equals("epam")) {
                    expected += j.getDuration();
                }
            }
        }

        int result = employees.stream()
                .map(Employee::getJobHistory)
                .flatMap(Collection::stream)
                .//????????????
                assertEquals(expected, result);
    }

}
