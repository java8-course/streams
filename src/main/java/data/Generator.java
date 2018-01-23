package data;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;

/**
 * @author Simon Popugaev
 */
public class Generator {

    public static String generateString() {
        final String letters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        final int maxLength = 10;
        final int length = ThreadLocalRandom.current().nextInt(maxLength) + 1;

        return IntStream.range(0, length)
                .mapToObj(letters::charAt)
                .map(Object::toString)
                .collect(Collectors.joining());
    }

    public static Person generatePerson() {
        return new Person(generateString(), generateString(), 18 + ThreadLocalRandom.current().nextInt(50));
    }

    public static JobHistoryEntry generateJobHistoryEntry() {
        final int maxDuration = 10;
        final int duration = ThreadLocalRandom.current().nextInt(maxDuration) + 1;
        return new JobHistoryEntry(duration, generatePosition(), generateEmployer());
    }

    public static String generateEmployer() {
        final String[] employers = {"epam", "google", "yandex", "abc"};

        return employers[ThreadLocalRandom.current().nextInt(employers.length)];
    }

    public static String generatePosition() {
        final String[] positions = {"dev", "QA", "BA"};

        return positions[ThreadLocalRandom.current().nextInt(positions.length)];
    }

    public static List<JobHistoryEntry> generateJobHistory() {
        int maxLength = 10;
        final int length = ThreadLocalRandom.current().nextInt(maxLength) + 1;

        return Stream.generate(Generator::generateJobHistoryEntry)
                .limit(length)
                .collect(toList());
    }

    public static Employee generateEmployee() {
        return new Employee(generatePerson(), generateJobHistory());
    }

    public static List<Employee> generateEmployeeList() {
        // TODO
        throw new UnsupportedOperationException();
    }

    /**
     * That method guarantees that returnable employees list contains
     * at least one employee with first and only experience at Epam.
     *
     * @return list of employee
     */
    public static List<Employee> generateEmployeeListWithEpamExperience() {
        final List<Employee> employees = generateEmployeeList();

        final Employee employeeWithFistEpamExperience = new Employee(
                new Person(
                        "Maria", "Cybershooter", 28
                ),
                Arrays.asList(
                        new JobHistoryEntry(
                                2, "dev", "epam"
                        ),
                        new JobHistoryEntry(
                                3, "QA", "epam"
                        )
                )
        );

        employees.add(employeeWithFistEpamExperience);

        return employees;
    }
}
