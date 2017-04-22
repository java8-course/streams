package part1.exercise;

import data.Employee;
import data.Generator;
import data.JobHistoryEntry;
import data.Person;
import org.junit.Test;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static data.Generator.generateEmployeeList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class StreamsExercise1 {
    // https://youtu.be/kxgo7Y4cdA8 Сергей Куксенко и Алексей Шипилёв — Через тернии к лямбдам, часть 1
    // https://youtu.be/JRBWBJ6S4aU Сергей Куксенко и Алексей Шипилёв — Через тернии к лямбдам, часть 2

    // https://youtu.be/O8oN4KSZEXE Сергей Куксенко — Stream API, часть 1
    // https://youtu.be/i0Jr2l3jrDA Сергей Куксенко — Stream API, часть 2

    private Stream<PersonEmployerPair> getEmployeePairs(Employee employee) {
        return employee.getJobHistory().stream()
                .map(JobHistoryEntry::getEmployer)
                .map(empl -> new PersonEmployerPair(employee.getPerson(), empl));
    }

    @Test
    public void getAllEpamEmployees() {
        final List<Employee> employees = Generator.generateEmployeeList();

        final Set<Person> people = employees.stream()
                .flatMap(this::getEmployeePairs)
                .filter(p -> p.getEmployer().equals("epam"))
                .map(PersonEmployerPair::getPerson)
                .collect(Collectors.toSet());

        for (Employee employee : employees) {
            for (JobHistoryEntry entry : employee.getJobHistory()) {
                if (entry.getEmployer().equals("epam")) {
                    assertTrue(people.contains(employee.getPerson()));
                }
            }
        }
    }

    @Test
    public void getEmployeesStartedFromEpam() {
        final List<Employee> employees = Generator.generateEmployeeList();

        final List<Person> personList = employees.stream()
                .filter(employee -> employee.getJobHistory().stream().limit(1)
                        .anyMatch(j -> j.getEmployer().equals("epam")))
                .map(Employee::getPerson)
                .collect(Collectors.toList());


        for (Employee employee : employees) {
            final String firstEmployer = employee.getJobHistory().get(0).getEmployer();
            for (Person person : personList) {
                if (firstEmployer.equals("epam") && person.equals(employee.getPerson())) {
                    assertEquals(employee.getPerson(), person);
                }
            }
        }
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


        final int result = employees.stream()
                .flatMap(e -> e.getJobHistory().stream())
                .filter(j -> j.getEmployer().equals("epam"))
                .mapToInt(JobHistoryEntry::getDuration)
                .sum();

        assertEquals(expected, result);
    }

    private static class PersonEmployerPair {
        private final Person person;
        private final String employer;

        public PersonEmployerPair(Person person, String employer) {
            this.person = person;
            this.employer = employer;
        }

        public Person getPerson() {
            return person;
        }

        public String getEmployer() {
            return employer;
        }
    }

}
