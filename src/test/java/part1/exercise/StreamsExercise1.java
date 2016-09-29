package part1.exercise;

import data.Employee;
import data.Generator;
import data.JobHistoryEntry;
import org.junit.Test;

import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;

public class StreamsExercise1 {
    // https://youtu.be/kxgo7Y4cdA8 Сергей Куксенко и Алексей Шипилёв — Через тернии к лямбдам, часть 1
    // https://youtu.be/JRBWBJ6S4aU Сергей Куксенко и Алексей Шипилёв — Через тернии к лямбдам, часть 2

    // https://youtu.be/O8oN4KSZEXE Сергей Куксенко — Stream API, часть 1
    // https://youtu.be/i0Jr2l3jrDA Сергей Куксенко — Stream API, часть 2

    private static List<Employee> employees = Generator.generateEmployeeList();

    private static final Predicate<String> employerWorkedForEpam = "epam"::equals;
    private static final Function<JobHistoryEntry, String> getEmployer = JobHistoryEntry::getEmployer;
    private static final Consumer<Employee> printEmployee = e -> {
        System.out.println(e.getPerson().toString());
        e.getJobHistory().stream()
                .peek(j -> System.out.println("\t\t".concat(j.toString())))
                .map(getEmployer)
                .anyMatch(employerWorkedForEpam);
    };

    @Test
    public void getAllEpamEmployees() {
        System.out.println("All persons with experience in EPAM:");
        employees.stream()
                .filter(e -> e.getJobHistory().stream()
                        .map(getEmployer)
                        .anyMatch(employerWorkedForEpam))
                .peek(printEmployee)
                .map(Employee::getPerson)
                .collect(Collectors.toList());
    }

    @Test
    public void getEmployeesStartedFromEpam() {
        System.out.println("All persons with first experience in EPAM:");
        employees.stream()
                .filter(e -> e.getJobHistory().stream()
                        .map(getEmployer)
                        .limit(1)
                        .anyMatch(employerWorkedForEpam))
                .peek(printEmployee)
                .map(Employee::getPerson)
                .collect(Collectors.toList());
    }

    @Test
    public void sumEpamDurations() {
        int expected = 0;

        for (Employee e : employees) {
            for (JobHistoryEntry j : e.getJobHistory()) {
                if (j.getEmployer().equals("epam")) {
                    expected += j.getDuration();
                }
            }
        }

        int result = employees.stream()
                .map(Employee::getJobHistory)
                .flatMap(Collection::stream)
                .filter(j -> employerWorkedForEpam.test(getEmployer.apply(j)))
                .mapToInt(JobHistoryEntry::getDuration)
                .sum();
        assertEquals(expected, result);
    }

}
