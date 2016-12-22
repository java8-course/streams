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

import static org.junit.Assert.assertEquals;

public class StreamsExercise1 {
    // https://youtu.be/kxgo7Y4cdA8 Сергей Куксенко и Алексей Шипилёв — Через тернии к лямбдам, часть 1
    // https://youtu.be/JRBWBJ6S4aU Сергей Куксенко и Алексей Шипилёв — Через тернии к лямбдам, часть 2

    // https://youtu.be/O8oN4KSZEXE Сергей Куксенко — Stream API, часть 1
    // https://youtu.be/i0Jr2l3jrDA Сергей Куксенко — Stream API, часть 2

    @Test
    public void getAllEpamEmployees() {
        final List<Employee> employees = Generator.generateEmployeeList();
        final List<Employee> expected = new ArrayList<>();
        for (Employee e : employees) {
            for (JobHistoryEntry entry : e.getJobHistory()){
                if ("epam".equals(entry.getEmployer())){
                    expected.add(e);
                    break;
                }
            }
        }
        final List<Employee> result = employees.stream()
                .filter(StreamsExercise1::hasExperienceInEpam)
                .collect(Collectors.toList());
        assertEquals(expected, result);
    }

    private static boolean hasExperienceInEpam(Employee e) {
        return e.getJobHistory().stream()
                .map(JobHistoryEntry::getEmployer)
                .anyMatch("epam"::equals);
    }

    @Test
    public void getEmployeesStartedFromEpam() {
        final List<Employee> employees = Generator.generateEmployeeList();
        final List<Employee> expected = new ArrayList<>();

        for (Employee e : employees) {
            final List<JobHistoryEntry> jobHistory = e.getJobHistory();
            if (jobHistory != null && !jobHistory.isEmpty()){
                if ("epam".equals(jobHistory.get(0).getEmployer())){
                    expected.add(e);
                }
            }
        }
        final List<Employee> result = employees.stream()
                .filter(StreamsExercise1::hasFirstExperienceInEpam)
                .collect(Collectors.toList());
        assertEquals(expected, result);
    }

    private static boolean hasFirstExperienceInEpam(Employee e) {
        return "epam".equals(e.getJobHistory().stream().findFirst().get().getEmployer());
    }

    @Test
    public void sumEpamDurations() {
        final List<Employee> employees = Generator.generateEmployeeList();

        int expected = 0;

        for (Employee e : employees) {
            for (JobHistoryEntry j : e.getJobHistory()) {
                if ("epam".equals(j.getEmployer())) {
                    expected += j.getDuration();
                }
            }
        }

        int result = employees.stream()
                .flatMap(e -> e.getJobHistory().stream())
                .filter(entry -> "epam".equals(entry.getEmployer()))
                .mapToInt(JobHistoryEntry::getDuration)
                .sum();
        assertEquals(expected, result);
    }

}
