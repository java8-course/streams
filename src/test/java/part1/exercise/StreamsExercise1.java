package part1.exercise;

import data.Employee;
import data.JobHistoryEntry;
import data.Person;
import org.junit.Test;

import java.util.*;

import static data.Generator.generateEmployeeList;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.summingInt;
import static java.util.stream.Collectors.toList;
import static org.junit.Assert.assertEquals;

public class StreamsExercise1 {
    // https://youtu.be/kxgo7Y4cdA8 Сергей Куксенко и Алексей Шипилёв — Через тернии к лямбдам, часть 1
    // https://youtu.be/JRBWBJ6S4aU Сергей Куксенко и Алексей Шипилёв — Через тернии к лямбдам, часть 2

    // https://youtu.be/O8oN4KSZEXE Сергей Куксенко — Stream API, часть 1
    // https://youtu.be/i0Jr2l3jrDA Сергей Куксенко — Stream API, часть 2

    @Test
    public void getAllEpamEmployees() {
        List<Employee> employees = generateEmployeeList();

        List<Person> personsList = employees.stream()
            .filter(e -> e.getJobHistory().stream()
                .map(JobHistoryEntry::getEmployer)
                .anyMatch(em -> em.equals("epam")))
            .map(Employee::getPerson)
            .collect(toList());

        List<Person> expectedPersonList = new ArrayList<>();
        for (Employee e : employees) {
            for (JobHistoryEntry j : e.getJobHistory()) {
                if (j.getEmployer().equals("epam")) {
                    expectedPersonList.add(e.getPerson());
                    break;
                }
            }
        }

        assertEquals(expectedPersonList, personsList);
    }

    @Test
    public void getEmployeesStartedFromEpam() {
        List<Employee> employees = generateEmployeeList();

        List<Person> personList = employees.stream()
            .filter(e -> e.getJobHistory().get(0).getEmployer().equals("epam"))
            .map(Employee::getPerson)
            .collect(toList());

        List<Person> epamEmployeesExpected = new ArrayList<>();
        for (Employee e : employees) {
            if (e.getJobHistory().get(0).getEmployer().equals("epam")) {
                epamEmployeesExpected.add(e.getPerson());
            }
        }

        assertEquals(epamEmployeesExpected, personList);
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
            .filter(j -> j.getEmployer().equals("epam"))
            .mapToInt(JobHistoryEntry::getDuration)
            .sum();

        assertEquals(expected, result);
    }

}
