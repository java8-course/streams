package part1.exercise;

import data.Employee;
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

import static org.junit.Assert.*;

public class StreamsExercise1 {
    // https://youtu.be/kxgo7Y4cdA8 Сергей Куксенко и Алексей Шипилёв — Через тернии к лямбдам, часть 1
    // https://youtu.be/JRBWBJ6S4aU Сергей Куксенко и Алексей Шипилёв — Через тернии к лямбдам, часть 2

    // https://youtu.be/O8oN4KSZEXE Сергей Куксенко — Stream API, часть 1
    // https://youtu.be/i0Jr2l3jrDA Сергей Куксенко — Stream API, часть 2

    @Test
    public void getAllEpamEmployees() {
//        List<Person> epamEmployees = null;// TODO all persons with experience in epam
        List<Employee> employees = generateEmployeeList();
        List<Person> epamEmployees = employees
                .stream()
                .filter(e ->
                        e.getJobHistory()
                                .stream()
                                .filter(h -> h.getEmployer().equals("epam"))
                                .count() > 0
                )
                .map(Employee::getPerson)
                .collect(toList());

        int count = 0;
        for (Employee e : employees) {
            for (JobHistoryEntry j : e.getJobHistory()) {
                if (j.getEmployer().equals("epam")) {
                    count++;
                    break;
                }
            }
        }
        assertEquals(count, epamEmployees.size());
//        throw new UnsupportedOperationException();
    }

    @Test
    public void getEmployeesStartedFromEpam() {
//        List<Person> epamEmployees = null;// TODO all persons with first experience in epam
        List<Employee> employees = generateEmployeeList();
        List<Person> epamEmployees = employees
                .stream()
                .filter(e ->
                        e.getJobHistory()
                                .stream()
                                .limit(1)
                                .filter(h -> h.getEmployer().equals("epam"))
                                .count() > 0
                )
                .map(Employee::getPerson)
                .collect(toList());

        int count = 0;
        for (Employee e : employees) {
            for (JobHistoryEntry j : e.getJobHistory()) {
                if (j.getEmployer().equals("epam")) {
                    count++;
                }
                break;
            }
        }
        assertEquals(count, epamEmployees.size());
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
//        throw new UnsupportedOperationException();

        int result = employees
                .stream()
                .mapToInt(e ->
                        e.getJobHistory()
                                .stream()
                                .filter(h -> h.getEmployer().equals("epam"))
                                .mapToInt(JobHistoryEntry::getDuration)
                                .sum()
                )
                .sum();
        assertEquals(expected, result);
    }

}
