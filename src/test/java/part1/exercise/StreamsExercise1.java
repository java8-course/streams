package part1.exercise;

import data.Employee;
import data.JobHistoryEntry;
import data.Person;
import org.junit.Test;

import java.util.*;
import java.util.stream.Collectors;

import static data.Generator.generateEmployeeList;
import static java.util.stream.Collectors.groupingBy;
import static org.junit.Assert.assertEquals;

public class StreamsExercise1 {
    // https://youtu.be/kxgo7Y4cdA8 Сергей Куксенко и Алексей Шипилёв — Через тернии к лямбдам, часть 1
    // https://youtu.be/JRBWBJ6S4aU Сергей Куксенко и Алексей Шипилёв — Через тернии к лямбдам, часть 2

    // https://youtu.be/O8oN4KSZEXE Сергей Куксенко — Stream API, часть 1
    // https://youtu.be/i0Jr2l3jrDA Сергей Куксенко — Stream API, часть 2

    @Test
    public void getAllEpamEmployees() {
        List<Employee> employees = generateEmployeeList();

        List<Person> expectedEpamEmployees = new ArrayList<>();
        for (Employee e : employees) {
            for (JobHistoryEntry j : e.getJobHistory()) {
                if (j.getEmployer().equals("epam")) {
                    expectedEpamEmployees.add(e.getPerson());
                }
            }
        }

        // TODO all persons with experience in epam - done
        List<Person> epamEmployees = employees.stream()
                .flatMap(e -> e.getJobHistory()
                        .stream()
                        .map(j -> new PersonEmployerDuration(e.getPerson(), j.getEmployer(), j.getDuration())))
                .filter(p -> p.getEmployer().equals("epam"))
                .map(p -> p.getPerson())
                .collect(Collectors.toList());

        assertEquals(expectedEpamEmployees, epamEmployees);
    }

    @Test
    public void getEmployeesStartedFromEpam() {
        List<Employee> employees = generateEmployeeList();

        List<Person> expectedStartedInEpamEmployees = new ArrayList<>();
        for (Employee e : employees) {
            Optional<JobHistoryEntry> job = e.getJobHistory().stream().findFirst();
            if (job.isPresent()) {
                if (job.get().getEmployer().equals("epam")) {
                    expectedStartedInEpamEmployees.add(e.getPerson());
                }
            }
        }

        // TODO all persons with first experience in epam - done
        List<Person> startedInEpamEmployees = employees.stream()
                .flatMap(e -> e.getJobHistory()
                        .stream()
                        .limit(1)
                        .map(j -> new PersonEmployerDuration(e.getPerson(), j.getEmployer(), j.getDuration())))
                .filter(j -> j.getEmployer().equals("epam"))
                .map(p -> p.getPerson())
                .collect(Collectors.toList());

        assertEquals(expectedStartedInEpamEmployees, startedInEpamEmployees);
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

        // TODO - done

        int result = employees.stream()
                .flatMap(employee -> employee.getJobHistory().stream())
                .filter(jobHistoryEntry -> jobHistoryEntry.getEmployer().equals("epam"))
                .mapToInt(JobHistoryEntry::getDuration)
                .sum();

        assertEquals(expected, result);
    }

    private static class PersonEmployerDuration {
        private final Person person;
        private final String employer;
        private final int duration;

        public PersonEmployerDuration(Person person, String employer, int duration) {
            this.person = person;
            this.employer = employer;
            this.duration = duration;
        }

        public Person getPerson() {
            return person;
        }

        public String getEmployer() {
            return employer;
        }

        public int getDuration() {
            return duration;
        }
    }

}
