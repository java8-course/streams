package part1.exercise;

import data.Employee;
import data.JobHistoryEntry;
import data.Person;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import static data.Generator.generateEmployeeList;
import static java.util.stream.Collectors.toList;


public class StreamsExercise1 {
    // https://youtu.be/kxgo7Y4cdA8 Сергей Куксенко и Алексей Шипилёв — Через тернии к лямбдам, часть 1
    // https://youtu.be/JRBWBJ6S4aU Сергей Куксенко и Алексей Шипилёв — Через тернии к лямбдам, часть 2

    // https://youtu.be/O8oN4KSZEXE Сергей Куксенко — Stream API, часть 1
    // https://youtu.be/i0Jr2l3jrDA Сергей Куксенко — Stream API, часть 2

    @Test
    public void getAllEpamEmployees() {
        List<Employee> epamEmployees = generateEmployeeList();// TODO all persons with experience in epam
        List<Person> personList = epamEmployees.stream().filter(e -> expEpam(e.getJobHistory())).map(Employee::getPerson).collect(toList());

        List<Person> expected = new LinkedList<>();
        for(Employee employee : epamEmployees) for( JobHistoryEntry entry : employee.getJobHistory())
                if(entry.getEmployer().equals("epam") && !expected.contains(employee.getPerson())) expected.add(employee.getPerson());

        Assert.assertEquals(expected, personList);
    }

    public static boolean expEpam(List<JobHistoryEntry> jobHistory) {
        return jobHistory.stream().anyMatch(e -> e.getEmployer().equals("epam"));
    }



    @Test
    public void getEmployeesStartedFromEpam() {
        List<Employee> epamEmployees = generateEmployeeList();// TODO all persons with first experience in epam
        List<Person> personList = epamEmployees.stream().filter(e -> e.getJobHistory().stream().allMatch(j->j.getEmployer().equals("epam"))).
                map(Employee::getPerson).collect(toList());

        List<Person> expected = new ArrayList<>();
        for (Employee employee : epamEmployees)
            if(employee.getJobHistory().stream().allMatch(e -> e.getEmployer().equals("epam")))
                expected.add(employee.getPerson());

        Assert.assertEquals(expected, personList);
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

        int result = employees.stream().flatMap(e -> e.getJobHistory().stream()).
                filter(v -> v.getEmployer().equals("epam")).mapToInt(JobHistoryEntry::getDuration).sum();
        Assert.assertEquals(expected, result);
    }
}
