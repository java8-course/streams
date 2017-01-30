package part1.exercise;

import data.Employee;
import data.Generator;
import data.JobHistoryEntry;
import data.Person;
import org.junit.Test;

import java.util.List;

import static java.util.stream.Collectors.toList;
import static org.junit.Assert.assertEquals;

public class StreamsExercise1 {
    // https://youtu.be/kxgo7Y4cdA8 Сергей Куксенко и Алексей Шипилёв — Через тернии к лямбдам, часть 1
    // https://youtu.be/JRBWBJ6S4aU Сергей Куксенко и Алексей Шипилёв — Через тернии к лямбдам, часть 2

    // https://youtu.be/O8oN4KSZEXE Сергей Куксенко — Stream API, часть 1
    // https://youtu.be/i0Jr2l3jrDA Сергей Куксенко — Stream API, часть 2

    @Test
    public void getAllEpamEmployees() {
        List<Person> epamEmployees = Generator.generateEmployeeList().stream()
                .filter(e -> e.getJobHistory().stream().anyMatch(j -> j.getEmployer().equals("epam")))
                .map(Employee::getPerson)
                .collect(toList());
    }

    @Test
    public void getEmployeesStartedFromEpam() {
        List<Person> epamEmployees = Generator.generateEmployeeList().stream()
                .filter(e -> e.getJobHistory().stream()
                        .findFirst()
                        .get()
                        .getEmployer().equals("epam"))
                .map(Employee::getPerson)
                .collect(toList());
    }

    @Test
    public void sumEpamDurations() {
        final List<Employee> employees = Generator.generateEmployeeList();

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
