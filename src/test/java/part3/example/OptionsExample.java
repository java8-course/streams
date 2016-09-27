package part3.example;

import data.Employee;
import data.JobHistoryEntry;
import data.Person;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collection;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Stream;

import static org.junit.Assert.assertEquals;

public class OptionsExample {
    private static Employee employee =
            new Employee(
                    new Person("John", "Galt", 30),
                    Arrays.asList(
                            new JobHistoryEntry(2, "dev", "epam")));

    private Optional<Employee> opt() {
        return ThreadLocalRandom.current().nextBoolean()
                ? Optional.of(employee)
                : Optional.empty();
    }


    @Test
    public void flatMap() {
        final Optional<Employee> opt = opt();

        final Optional<JobHistoryEntry> firstQaExperience1;

        if (opt.isPresent()) {
            firstQaExperience1 = opt.get().getJobHistory().stream().findFirst();
        } else {
            firstQaExperience1 = Optional.empty();
        }

        final Optional<JobHistoryEntry> firstQaExperience2 =
                opt.flatMap(e -> e.getJobHistory().stream().findFirst());

        assertEquals(firstQaExperience1, firstQaExperience2);

        final Optional<JobHistoryEntry> firstQaExperience3 =
                opt
                        .map(Employee::getJobHistory)
                        .map(Collection::stream)
                        .flatMap(Stream::findFirst);

        assertEquals(firstQaExperience1, firstQaExperience3);
    }

    @Test
    public void map() {
        final Optional<Employee> opt = opt();

        final Optional<Person> person1;

        if (opt.isPresent()) {
            person1 = Optional.of(opt.get().getPerson());
        } else {
            person1 = Optional.empty();
        }

        final Optional<Person> person2 = opt.map(Employee::getPerson);

        assertEquals(person1, person2);
    }

    @Test
    public void filter() {
        final Optional<Employee> opt = opt();

        final Optional<Employee> e1;

        if (opt.isPresent() && opt.get().getPerson().getAge() > 18) {
            e1 = opt;
        } else {
            e1 = Optional.empty();
        }

        final Optional<Employee> e2 =
                opt.filter(e -> e.getPerson().getAge() > 18);

        assertEquals(e1, e2);
    }

    @Test
    public void forEach() {
        final Optional<Employee> opt = opt();

        if (opt.isPresent()) {
            System.out.println(opt.get());
        }

        opt.ifPresent(System.out::println);
    }

    @Test
    public void orElseGet() {
        final Optional<Employee> opt = opt();

        final Person personOrDefault1;

        if (opt.isPresent()) {
            personOrDefault1 = opt.get().getPerson();
        } else {
            personOrDefault1 = new Person("a", "b", 0);
        }

        final Person personOrDefault2 =
                opt
                .map(Employee::getPerson)
                .orElseGet(() -> new Person("a", "b", 0));

        assertEquals(personOrDefault1, personOrDefault2);
    }
}
