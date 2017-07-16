package part1.exercise;

import data.Employee;
import data.Generator;
import data.JobHistoryEntry;
import data.Person;
import org.junit.Test;

import java.util.List;

import static data.Generator.generateEmployeeList;
import static java.util.stream.Collectors.summarizingInt;
import static java.util.stream.Collectors.toList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class StreamsExercise1 {
    // https://youtu.be/kxgo7Y4cdA8 Сергей Куксенко и Алексей Шипилёв — Через тернии к лямбдам, часть 1
    // https://youtu.be/JRBWBJ6S4aU Сергей Куксенко и Алексей Шипилёв — Через тернии к лямбдам, часть 2

    // https://youtu.be/O8oN4KSZEXE Сергей Куксенко — Stream API, часть 1
    // https://youtu.be/i0Jr2l3jrDA Сергей Куксенко — Stream API, часть 2

    @Test
    public void getAllEpamEmployees() {
        List<Employee> employees = Generator.generateEmployeeList();

        List<Person> epamEmployees =
                employees.stream()
                        .filter(employee ->
                                employee.getJobHistory()
                                        .stream()
                                        .anyMatch(
                                                job -> job.getEmployer().equals("epam")
                                        )
                        )
                        .map(Employee::getPerson)
                        .collect(toList());

        employees.forEach(employee ->
                {
                    epamEmployees.forEach(
                            person -> {
                                if (person.equals(employee.getPerson())) {
                                    assertTrue(
                                            employee.getJobHistory()
                                                    .stream()
                                                    .anyMatch(
                                                            job -> job.getEmployer().equals("epam")
                                                    )
                                    );
                                }
                            }
                    );
                }
        );

        long epamEmployeesCount =
                employees.stream()
                        .filter(
                                employee -> employee.getJobHistory()
                                        .stream()
                                        .anyMatch(job -> job.getEmployer().equals("epam"))
                        )
                        .count();

        assertEquals(epamEmployees.size(), epamEmployeesCount);
    }

    @Test
    public void getEmployeesStartedFromEpam() {
        List<Employee> employees = Generator.generateEmployeeList();
        List<Person> epamEmployees =
                employees.stream()
                        .filter(
                                employee -> employee.getJobHistory()
                                        .stream()
                                        .findFirst()
                                        .filter(job -> job.getEmployer().equals("epam"))
                                        .isPresent()
                        )
                        .map(Employee::getPerson)
                        .collect(toList());

        employees.forEach(employee ->
                {
                    epamEmployees.forEach(
                            person -> {
                                if (person.equals(employee.getPerson())) {
                                    employee.getJobHistory()
                                            .stream()
                                            .findFirst()
                                            .ifPresent(
                                                    job -> assertEquals("epam", job.getEmployer())
                                            );
                                }
                            }
                    );
                }
        );

        long epamEmployeesCount =
                employees.stream()
                        .filter(
                                employee -> employee.getJobHistory()
                                        .stream()
                                        .findFirst()
                                        .filter(job -> job.getEmployer().equals("epam"))
                                        .isPresent()
                        )
                        .count();

        assertEquals(epamEmployees.size(), epamEmployeesCount);

    }

    @Test
    public void sumEpamDurations() {
        final List<Employee> employees = generateEmployeeList();

        long expected = 0;

        for (Employee e : employees) {
            for (JobHistoryEntry j : e.getJobHistory()) {
                if (j.getEmployer().equals("epam")) {
                    expected += j.getDuration();
                }
            }
        }

        long result =
                employees.stream()
                        .flatMap(employee ->
                                employee.getJobHistory().stream()
                                        .filter(job -> job.getEmployer().equals("epam"))
                        )
                        .collect(
                                summarizingInt(JobHistoryEntry::getDuration)
                        )
                        .getSum();

        assertEquals(expected, result);
    }

}
