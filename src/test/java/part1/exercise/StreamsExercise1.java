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
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.mapping;
import static java.util.stream.Collectors.toList;
import static org.junit.Assert.assertEquals;

public class StreamsExercise1 {
    // https://youtu.be/kxgo7Y4cdA8 Сергей Куксенко и Алексей Шипилёв — Через тернии к лямбдам, часть 1
    // https://youtu.be/JRBWBJ6S4aU Сергей Куксенко и Алексей Шипилёв — Через тернии к лямбдам, часть 2

    // https://youtu.be/O8oN4KSZEXE Сергей Куксенко — Stream API, часть 1
    // https://youtu.be/i0Jr2l3jrDA Сергей Куксенко — Stream API, часть 2

    @Test
    public void getAllEpamEmployees() {
        final List<Employee> epamEmployees = Generator.generateEmployeeList();
        final List<Employee> exp = new ArrayList<>();
        for (Employee e: epamEmployees) {
            for (JobHistoryEntry entry : e.getJobHistory()){
                if (entry.getEmployer().equals("epam")) {
                    exp.add(e);
                    break;
                }
            }
        }
        final List<Employee> result = epamEmployees.stream()
                .filter(StreamsExercise1::wasInEpam)
                .collect(toList());
        assertEquals(exp,result);
    }

    private static boolean wasInEpam(Employee e){
        return e.getJobHistory().stream().map(JobHistoryEntry::getEmployer)
                .anyMatch("epam"::equals);
    }

    public static boolean epamStart (Employee e) {
        return e.getJobHistory().stream()
                .limit(1)
                .map(JobHistoryEntry::getEmployer)
                .anyMatch("epam"::equals);
    }

    @Test
    public void getEmployeesStartedFromEpam() {
        List<Employee> epamEmployees = Generator.generateEmployeeList();// TODO all persons with first experience in epam
        List<Employee> exp = new ArrayList<>();
        for (Employee e : epamEmployees) {
            if (e.getJobHistory().get(0).getEmployer().equals("epam")) exp.add(e);
        }
        final List<Employee> res = epamEmployees.stream()
                .filter(StreamsExercise1::epamStart)
                .collect(toList());
        assertEquals(exp,res);
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
                .filter(en -> en.getEmployer().equals("epam"))
                .mapToInt(JobHistoryEntry::getDuration)
                .sum();
        assertEquals(expected, result);
    }

}
