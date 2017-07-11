package part1.exercise;

import data.Employee;
import data.Generator;
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

    public List<JobHistoryEntry> getEpamOffer(List<JobHistoryEntry> jobs) {
        return jobs.stream().filter(j -> j.getEmployer().equals("epam")).collect(toList());
    }

    @Test
    public void getAllEpamEmployees() {
        final List<Employee> employees    = Generator.generateEmployeeList();
        final List<Person>   badCalculate = new ArrayList<>();

        for (Employee e : employees) {
            for (JobHistoryEntry j : e.getJobHistory()) {
                if (j.getEmployer().equals("epam")) {
                    badCalculate.add(e.getPerson());
                    break;
                }
            }
        }

        final List<Person> epamEmployees = employees.stream()
                .filter(e -> !getEpamOffer(e.getJobHistory()).isEmpty())
                .map(Employee::getPerson)
                .collect(toList());
        assertEquals(badCalculate, epamEmployees);
    }

    @Test
    public void getEmployeesStartedFromEpam() {
        final List<Employee> employees    = Generator.generateEmployeeList();
        final List<Person>   badCalculate = new ArrayList<>();

        for (final Employee e : employees) {
            if ("epam".equals(e.getJobHistory().get(0).getEmployer())) {
                badCalculate.add(e.getPerson());
            }
        }

        final List<Person> epamEmployees = employees.stream()
                .filter(e -> "epam".equals(e.getJobHistory().get(0).getEmployer()))
                .map(Employee::getPerson)
                .collect(toList());

        assertEquals(badCalculate, epamEmployees);
    }

    @Test
    public void sumEpamDurations() {
        final List<Employee> employees = generateEmployeeList();

        int expected = 0;


        for (final Employee e : employees) {
            for (JobHistoryEntry j : e.getJobHistory()) {
                if (j.getEmployer().equals("epam")) {
                    expected += j.getDuration();
                }
            }
        }


        int result = employees.stream()
                .flatMap(e -> e.getJobHistory().stream())
                .filter(j -> "epam".equals(j.getEmployer()))
                .mapToInt(JobHistoryEntry::getDuration)
                .sum();
        assertEquals(expected, result);
    }

}
