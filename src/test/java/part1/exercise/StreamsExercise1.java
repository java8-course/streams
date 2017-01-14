package part1.exercise;

import data.Employee;
import data.JobHistoryEntry;
import data.Person;
import org.junit.Test;

import java.util.*;

import static data.Generator.generateEmployeeList;
import static java.util.stream.Collectors.toList;
import static org.junit.Assert.assertEquals;

public class StreamsExercise1 {
    // https://youtu.be/kxgo7Y4cdA8 Сергей Куксенко и Алексей Шипилёв — Через тернии к лямбдам, часть 1
    // https://youtu.be/JRBWBJ6S4aU Сергей Куксенко и Алексей Шипилёв — Через тернии к лямбдам, часть 2

    // https://youtu.be/O8oN4KSZEXE Сергей Куксенко — Stream API, часть 1
    // https://youtu.be/i0Jr2l3jrDA Сергей Куксенко — Stream API, часть 2

    @Test
    public void getAllEpamEmployees() {
        // TODO all persons with experience in epam

        List<Employee> employees = generateEmployeeList();

        List<Person> epamEmployees = employees.stream()
                .filter(e -> hasExperienceInEpam(e.getJobHistory()))
                .map(Employee::getPerson)
                .collect(toList());

        List<Person> expected = new ArrayList<>();
        for(Employee employee : employees){
            for(JobHistoryEntry entry : employee.getJobHistory()){
                if(entry.getEmployer().equals("epam") && !expected.contains(employee.getPerson())){
                    expected.add(employee.getPerson());
                }
            }
        }

        assertEquals(expected, epamEmployees);
    }

    public static boolean hasExperienceInEpam(List<JobHistoryEntry> JobHistory){
        return JobHistory.stream()
                .filter(j -> j.getEmployer().equals("epam"))
                .collect(toList())
                .size() > 0;
    }


    @Test
    public void getEmployeesStartedFromEpam() {
        // TODO all persons with first experience in epam

        List<Employee> employees = generateEmployeeList();

        List<Person> epamEmployees = employees.stream()
                .filter(e -> e.getJobHistory().get(0).getEmployer().equals("epam"))
                .map(Employee::getPerson)
                .collect(toList());

        System.out.println("Actual size: " + epamEmployees.size());

        List<Person> expected = new ArrayList<>();
        for(Employee e : employees){
            if(e.getJobHistory().get(0).getEmployer().equals("epam")){
                expected.add(e.getPerson());
            }
        }
        System.out.println("Expected size: " + expected.size());

        assertEquals(expected, epamEmployees);
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
                 .filter(j -> j.getEmployer().equals("epam"))
                 .mapToInt(JobHistoryEntry::getDuration)
                 .sum();

         assertEquals(expected, result);
    }

}
