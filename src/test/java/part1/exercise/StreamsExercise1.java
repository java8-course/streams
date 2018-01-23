package part1.exercise;

import data.Employee;
import data.Generator;
import data.JobHistoryEntry;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static data.Generator.generateEmployeeList;
import static junit.framework.TestCase.assertFalse;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class StreamsExercise1 {
    // https://youtu.be/kxgo7Y4cdA8 Сергей Куксенко и Алексей Шипилёв — Через тернии к лямбдам, часть 1
    // https://youtu.be/JRBWBJ6S4aU Сергей Куксенко и Алексей Шипилёв — Через тернии к лямбдам, часть 2

    // https://youtu.be/O8oN4KSZEXE Сергей Куксенко — Stream API, часть 1
    // https://youtu.be/i0Jr2l3jrDA Сергей Куксенко — Stream API, часть 2

    @Test
    public void getAllEpamEmployees() {
        List<Employee> allEmployee = Generator.generateEmployeeListWithEpamExperience();

        final List<Employee> expected = new ArrayList<>();

        for (Employee employee : allEmployee) {
            final List<JobHistoryEntry> jobHistory = employee.getJobHistory();
            boolean isEpamEmployee = false;
            for (JobHistoryEntry jobHistoryEntry : jobHistory) {
                if("epam".equals(jobHistoryEntry.getEmployer())) {
                    isEpamEmployee = true;
                }
            }
            if (isEpamEmployee) {
                expected.add(employee);
            }
        }

        List<Employee> epamEmployees = null;
        // TODO all persons with experience in epam


        assertTrue("Expected size" + expected.size(), expected.size() == epamEmployees.size());
        assertTrue("Wrong result", expected.containsAll(epamEmployees));
    }

    @Test
    public void getEmployeesStartedFromEpam() {
        List<Employee> allEmployee = Generator.generateEmployeeListWithEpamExperience();

        final List<Employee> expected = new ArrayList<>();

        for (Employee employee : allEmployee) {
            if("epam".equals(employee.getJobHistory().iterator().next().getEmployer())) {
                expected.add(employee);
            }
        }

        List<Employee> epamEmployees = null;
        // TODO all persons with first experience in epam

        assertNotNull(epamEmployees);
        assertFalse(epamEmployees.isEmpty());

        assertTrue("Expected size" + expected.size(), expected.size() == epamEmployees.size());
        assertTrue("Wrong result", expected.containsAll(epamEmployees));
    }

    @Test
    public void sumEpamDurations() {
        final List<Employee> employees = generateEmployeeList();

        Integer expected = 0;

        for (Employee e : employees) {
            for (JobHistoryEntry j : e.getJobHistory()) {
                if (j.getEmployer().equals("epam")) {
                    expected += j.getDuration();
                }
            }
        }

         Integer result = null;//TODO sum of all durations in epam job histories
         assertEquals(expected, result);
    }

}
