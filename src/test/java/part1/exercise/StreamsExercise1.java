package part1.exercise;

import data.Employee;
import data.Generator;
import data.JobHistoryEntry;
import data.Person;
import org.junit.Assert;
import org.junit.Test;

import java.util.*;

import static data.Generator.generateEmployeeList;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;

public class StreamsExercise1 {
    // https://youtu.be/kxgo7Y4cdA8 Сергей Куксенко и Алексей Шипилёв — Через тернии к лямбдам, часть 1
    // https://youtu.be/JRBWBJ6S4aU Сергей Куксенко и Алексей Шипилёв — Через тернии к лямбдам, часть 2

    // https://youtu.be/O8oN4KSZEXE Сергей Куксенко — Stream API, часть 1
    // https://youtu.be/i0Jr2l3jrDA Сергей Куксенко — Stream API, часть 2

    private static boolean hasEpamExperience(List<JobHistoryEntry> list) {
        return list.stream().anyMatch(j -> j.getEmployer().equals("epam"));
    }

    @Test
    public void getAllEpamEmployees() {
        List<Employee> employees = Generator.generateEmployeeList();
        List<Person> epamEmployees = employees
                .stream()
                .filter(e -> hasEpamExperience(e.getJobHistory()))
                .map(Employee::getPerson)
                .collect(toList());// TODO all persons with experience in epam

        List<Person> epamEmployeesExpected = new ArrayList<>();
        for (Employee e : employees) {
            for (JobHistoryEntry j : e.getJobHistory()) {
                if (j.getEmployer().equals("epam")) {
                    epamEmployeesExpected.add(e.getPerson());
                    break;
                }
            }
        }

        Assert.assertArrayEquals(epamEmployees.toArray(), epamEmployeesExpected.toArray());
    }

    private static boolean hasStartedEpamExperience(List<JobHistoryEntry> list) {
        return list.stream()
                .findFirst()
                .orElse(new JobHistoryEntry(0, "", ""))
                .getEmployer()
                .equals("epam");
    }

    @Test
    public void getEmployeesStartedFromEpam() {
        List<Employee> employees = Generator.generateEmployeeList();
        List<Person> epamEmployees = employees
                .stream()
                .filter(e -> hasStartedEpamExperience(e.getJobHistory()))
                .map(Employee::getPerson)
                .collect(toList());// TODO all persons with first experience in epam

        List<Person> epamEmployeesExpected = new ArrayList<>();
        for (Employee e : employees) {
            if (e.getJobHistory().get(0).getEmployer().equals("epam")) {
                epamEmployeesExpected.add(e.getPerson());
            }
        }

        Assert.assertArrayEquals(epamEmployees.toArray(), epamEmployeesExpected.toArray());
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
                .map(JobHistoryEntry::getDuration)
                .reduce(Integer::sum)
                .orElse(0);

        Assert.assertEquals(expected, result);
    }

}
