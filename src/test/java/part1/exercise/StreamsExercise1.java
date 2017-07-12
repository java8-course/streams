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
import static java.util.stream.Collectors.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class StreamsExercise1 {
    // https://youtu.be/kxgo7Y4cdA8 Сергей Куксенко и Алексей Шипилёв — Через тернии к лямбдам, часть 1
    // https://youtu.be/JRBWBJ6S4aU Сергей Куксенко и Алексей Шипилёв — Через тернии к лямбдам, часть 2

    // https://youtu.be/O8oN4KSZEXE Сергей Куксенко — Stream API, часть 1
    // https://youtu.be/i0Jr2l3jrDA Сергей Куксенко — Stream API, часть 2

    @Test
    public void getAllEpamEmployees() {
        // TODO all persons with experience in epam
        final List<Employee> employees = Generator.generateEmployeeList();
        List<Person> epamEmployees = employees.stream()
                .filter(e -> e.getJobHistory().stream().anyMatch(j -> j.getPosition().equals("epam")))
                .map(Employee::getPerson)
                .collect(Collectors.toList());

        assertTrue(epamEmployees.stream()
                .allMatch(p -> employees.stream().anyMatch(e ->
                        e.getPerson().equals(p) && e.getJobHistory().stream().anyMatch(j -> j.getPosition().equals("epam")))));
    }

    @Test
    public void getEmployeesStartedFromEpam() {
        // TODO all persons with first experience in epam
        final List<Employee> employees = Generator.generateEmployeeList();
        List<Person> epamEmployees = employees.stream()
                .filter(e -> e.getJobHistory().get(0).getPosition().equals("epam"))
                .map(Employee::getPerson)
                .collect(Collectors.toList());

        assertTrue(epamEmployees.stream()
                .allMatch(p -> employees.stream().anyMatch(e ->
                        e.getPerson().equals(p) && e.getJobHistory().get(0).getPosition().equals("epam"))));
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
        int actual = employees.stream()
                .flatMap(e -> e.getJobHistory().stream())
                .filter(j -> j.getEmployer().equals("epam"))
                .mapToInt(JobHistoryEntry::getDuration)
                .sum();

        assertEquals(expected, actual);
    }

}
