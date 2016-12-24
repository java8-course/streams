package part1.exercise;

import data.Employee;
import data.JobHistoryEntry;
import data.Person;
import org.junit.Test;
import static org.junit.Assert.assertEquals;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static data.Generator.generateEmployeeList;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.mapping;
import static java.util.stream.Collectors.toList;

public class StreamsExercise1 {
    // https://youtu.be/kxgo7Y4cdA8 Сергей Куксенко и Алексей Шипилёв — Через тернии к лямбдам, часть 1
    // https://youtu.be/JRBWBJ6S4aU Сергей Куксенко и Алексей Шипилёв — Через тернии к лямбдам, часть 2

    // https://youtu.be/O8oN4KSZEXE Сергей Куксенко — Stream API, часть 1
    // https://youtu.be/i0Jr2l3jrDA Сергей Куксенко — Stream API, часть 2

    static boolean hasEpamExperience(List<JobHistoryEntry> jobHistory){
        return jobHistory.stream()
                .map(JobHistoryEntry::getEmployer)
                .anyMatch("epam"::equals);
    }

    @Test
    public void getAllEpamEmployees() {
        final List<Employee> employees = generateEmployeeList();

        List<Person> epamEmployees = employees.stream()
                .filter(e -> hasEpamExperience(e.getJobHistory()))
                .map(Employee::getPerson)
                .collect(toList());
        // TODO all persons with experience in epam
        assertEquals(false, epamEmployees.isEmpty());
    }

    @Test
    public void getEmployeesStartedFromEpam() {
        final List<Employee> employees = generateEmployeeList();
        
        List<Person> epamEmployees = employees.stream()
                .filter(e -> hasEpamExperience(e.getJobHistory()))
                .filter(e -> e.getJobHistory().get(0).getEmployer().equals("epam"))
                .map(Employee::getPerson)
                .collect(toList());
        // TODO all persons with first experience in epam

        assertEquals(false, epamEmployees.isEmpty());
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
                 .filter(e -> hasEpamExperience(e.getJobHistory()))
                 .map(e -> e.getJobHistory())
                 .flatMap(List::stream)
                 .filter(e -> e.getEmployer().equals("epam"))
                 .mapToInt(e -> e.getDuration())
                 .sum();
        assertEquals(expected, result);
    }

}
