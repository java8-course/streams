package part1.exercise;

import data.Employee;
import data.JobHistoryEntry;
import data.Person;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static data.Generator.generateEmployeeList;
import static java.util.stream.Collectors.toList;
import static org.junit.Assert.assertEquals;

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
        List<Person> actual = employees.stream()
                .filter(e -> e.getJobHistory().stream()
                        .map(JobHistoryEntry::getEmployer)
                        .anyMatch("epam"::equals))
                .map(Employee::getPerson)
                .collect(toList());

        assertEquals(expected, actual);
    }

    @Test
    public void getEmployeesStartedFromEpam() {
        final List<Employee> employees = generateEmployeeList();

        List<Person> expected = new ArrayList<>();

        for (Employee e : employees) {
            if (isFirstEmployerEpam(e))
                expected.add(e.getPerson());
        }
        List<Person> actual = employees.stream()
                .filter(StreamsExercise1::isFirstEmployerEpam)
                .map(Employee::getPerson)
                .collect(toList());

        assertEquals(expected, actual);
    }

    private static boolean isFirstEmployerEpam(Employee e) {
        return e.getJobHistory()
                .stream()
                .limit(1)
                .anyMatch("epam"::equals);
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
                .map(Employee::getJobHistory)
                .flatMap(e -> e.stream()
                        .filter(h -> h.getEmployer().equals("epam")))
                .mapToInt(JobHistoryEntry::getDuration)
                .sum();
        assertEquals(expected, result);
    }
}
