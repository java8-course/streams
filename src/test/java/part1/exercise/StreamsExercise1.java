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
import static org.junit.Assert.assertEquals;

public class StreamsExercise1 {
    // https://youtu.be/kxgo7Y4cdA8 Сергей Куксенко и Алексей Шипилёв — Через тернии к лямбдам, часть 1
    // https://youtu.be/JRBWBJ6S4aU Сергей Куксенко и Алексей Шипилёв — Через тернии к лямбдам, часть 2

    // https://youtu.be/O8oN4KSZEXE Сергей Куксенко — Stream API, часть 1
    // https://youtu.be/i0Jr2l3jrDA Сергей Куксенко — Stream API, часть 2

    @Test
    public void getAllEpamEmployees() {
        final List<Employee> employees = generateEmployeeList();
        List<Person> expected = new ArrayList<>();

        for (final Employee employee : employees) {
            if (hasEpamExperience(employee)) {
                expected.add(employee.getPerson());
            }
        }

        List<Person> epamEmployees = employees.stream()
                .filter(this::hasEpamExperience)
                .map(Employee::getPerson)
                .collect(Collectors.toList());

        assertEquals(expected, epamEmployees);
    }

    private boolean hasEpamExperience(Employee employee) {
        return employee.getJobHistory().stream()
                .anyMatch(jobHistoryEntry -> jobHistoryEntry.getEmployer().equals("epam"));
    }

    static class PersonFirstExperience {
        private Person person;
        private JobHistoryEntry firstJob;

        public PersonFirstExperience(Person person, JobHistoryEntry firstJob) {
            this.person = person;
            this.firstJob = firstJob;
        }

        public Person getPerson() {
            return person;
        }

        public JobHistoryEntry getFirstJob() {
            return firstJob;
        }

        public static Optional<PersonFirstExperience> fromEmployee(Employee employee) {
            if (employee.getJobHistory().size() < 1) {
                return Optional.empty();
            }

            return Optional.of(new PersonFirstExperience(employee.getPerson(), employee.getJobHistory().get(0)));
        }
    }

    @Test
    public void getEmployeesStartedFromEpam() {
        List<Employee> employees = generateEmployeeList();

        List<Person> expected = new ArrayList<>();

        for (final Employee employee : employees) {
            if (employee.getJobHistory().size() > 1 && employee.getJobHistory().get(0).getEmployer().equals("epam")) {
                expected.add(employee.getPerson());
            }
        }

        List<Person> epamEmployees = employees.stream()
                .map(PersonFirstExperience::fromEmployee)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .filter(this::withEpamFirstJob)
                .map(PersonFirstExperience::getPerson)
                .collect(toList());

        assertEquals(expected, epamEmployees);
    }

    private boolean withEpamFirstJob(PersonFirstExperience personFirstExperience) {
        return personFirstExperience.getFirstJob().getEmployer().equals("epam");
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
                .flatMap(
                        employee -> employee.getJobHistory().stream()
                )
                .filter(jobHistoryEntry -> jobHistoryEntry.getEmployer().equals("epam"))
                .mapToInt(JobHistoryEntry::getDuration)
                .sum();

        assertEquals(expected, result);
    }

}
