package part1.exercise;

import data.Employee;
import data.JobHistoryEntry;
import data.Person;
import org.junit.Test;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static data.Generator.generateEmployeeList;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.mapping;
import static java.util.stream.Collectors.toList;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

public class StreamsExercise1 {
    // https://youtu.be/kxgo7Y4cdA8 Сергей Куксенко и Алексей Шипилёв — Через тернии к лямбдам, часть 1
    // https://youtu.be/JRBWBJ6S4aU Сергей Куксенко и Алексей Шипилёв — Через тернии к лямбдам, часть 2

    // https://youtu.be/O8oN4KSZEXE Сергей Куксенко — Stream API, часть 1
    // https://youtu.be/i0Jr2l3jrDA Сергей Куксенко — Stream API, часть 2

    @Test
    public void getAllEpamEmployees() {
        final List<Employee> employees = generateEmployeeList();

        List<Person> expected = new ArrayList<>();

        for (Employee e : employees){
            for (JobHistoryEntry j : e.getJobHistory())
                if (j.getEmployer().equals("epam")){
                    expected.add(e.getPerson());
                    break;
                }
        }

        List<Person> actual = employees.stream()
                .filter(this::hasExperienceInEpam)
                .map(Employee::getPerson)
                .collect(toList());


        assertThat(actual, equalTo(expected));
    }

    private boolean hasExperienceInEpam(Employee employee){
        return employee.getJobHistory().stream()
                .filter(j -> j.getEmployer().equals("epam"))
                .count() > 0;
    }

    @Test
    public void getEmployeesStartedFromEpam() {
        final List<Employee> employees = generateEmployeeList();

        List<Person> expected = new ArrayList<>();

        for (Employee e : employees){
            if (e.getJobHistory().size() > 0 && e.getJobHistory().get(0).getEmployer().equals("epam"))
                expected.add(e.getPerson());
        }

        List<Person> actual = employees.stream()
                .filter(this::startExperienceInEpam)
                .map(Employee::getPerson)
                .collect(toList());

        assertThat(actual, equalTo(expected));
    }

    private boolean startExperienceInEpam(Employee employee) {
        return employee.getJobHistory().stream()
                .limit(1)
                .filter(j -> j.getEmployer().equals("epam"))
                .count() > 0;
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
                .flatMap(employee -> employee.getJobHistory().stream())
                .filter(j -> j.getEmployer().equals("epam"))
                .mapToInt(JobHistoryEntry::getDuration)
                .sum();
        
        assertEquals(expected, result);
    }

}
