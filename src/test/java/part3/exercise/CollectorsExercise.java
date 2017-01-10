package part3.exercise;

import data.Employee;
import data.JobHistoryEntry;
import data.Person;
import org.junit.Assert;
import org.junit.Test;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;
import java.util.stream.Collectors;

/**
 * Created by hamster on 10.01.17.
 */
public class CollectorsExercise {

    public <T> Collector<T, Set<T>, Set<T>> toSet() {
        return new Collector<T, Set<T>, Set<T>>() {
            @Override
            public Supplier<Set<T>> supplier() {
                return HashSet::new;
            }

            @Override
            public BiConsumer<Set<T>, T> accumulator() {
                return Set::add;
            }

            @Override
            public BinaryOperator <Set<T>> combiner() {
                return (s1, s2) -> {
                    s1.addAll(s2);
                    return s1;
                };
            }

            @Override
            public Function <Set<T>, Set <T>> finisher() {
                return null;
            }

            @Override
            public Set<Characteristics> characteristics() {
                return Collections.unmodifiableSet(EnumSet.of(
                        Characteristics.UNORDERED,
                        Characteristics.IDENTITY_FINISH));
            }
        };
    }

    @Test
    public void toSetTest() {
        Set expected = getEmployees().stream().collect(Collectors.toSet());
        Set actual = getEmployees().stream().collect(toSet());
        Assert.assertEquals(expected, actual);
    }


    public <T, U, S, R> Collector <T, ?, R> mapping(Function <? super T, ? extends U> mapper,
                                                    Collector <? super U, S, R> collector) {
        return new Collector <T, S, R> () {
            @Override
            public Supplier<S> supplier() {
                return collector.supplier();
            }

            @Override
            public BiConsumer <S, T> accumulator() {
                return (r, t) -> collector.accumulator().accept(r, mapper.apply(t));
            }

            @Override
            public BinaryOperator<S> combiner() {
                return collector.combiner();
            }

            @Override
            public Function <S, R> finisher() {
                return collector.finisher();
            }

            @Override
            public Set<Characteristics> characteristics() {
                return collector.characteristics();
            }
        };
    }

    @Test
    public void mappingTest() {
        Set expected = getEmployees().stream().collect(Collectors.mapping(Employee::getPerson, toSet()));
        Set actual = getEmployees().stream().collect(mapping(Employee::getPerson, toSet()));
        Assert.assertEquals(expected, actual);
    }

    public <T, S, R1, R2> Collector <T, S, R2> collectAndThen(Collector <T, S, R1> collector,
                                                              Function<R1, R2> function) {
        return new Collector <T, S, R2> () {
            @Override
            public Supplier <S> supplier() {
                return collector.supplier();
            }

            @Override
            public BiConsumer <S, T> accumulator() {
                return collector.accumulator();
            }

            @Override
            public BinaryOperator <S> combiner() {
                return collector.combiner();
            }

            @Override
            public Function <S, R2> finisher() {
                return collector.finisher().andThen(function);
            }

            @Override
            public Set<Characteristics> characteristics() {
                return collector.characteristics();
            }
        };

    }

    @Test
    public void collectAndThenTest() {
        String expected = getEmployees()
                .stream()
                .flatMap(e -> e.getJobHistory().stream())
                .collect(Collectors.collectingAndThen(Collectors.maxBy(Comparator.comparing(JobHistoryEntry::getDuration)),
                        j -> j.get().getEmployer()));
        String actual = getEmployees()
                .stream()
                .flatMap(e -> e.getJobHistory().stream())
                .collect(collectAndThen(Collectors.maxBy(Comparator.comparing(JobHistoryEntry::getDuration)),
                        j -> j.get().getEmployer()));
        Assert.assertEquals(expected, actual);
    }

    private List<Employee> getEmployees() {
        return Arrays.asList(
                new Employee(
                        new Person("John", "Galt", 20),
                        Collections.emptyList()),
                new Employee(
                        new Person("John", "Doe", 21),
                        Arrays.asList(
                                new JobHistoryEntry(4, "BA", "yandex"),
                                new JobHistoryEntry(2, "QA", "epam"),
                                new JobHistoryEntry(2, "dev", "abc")
                        )),
                new Employee(
                        new Person("John", "White", 22),
                        Collections.singletonList(
                                new JobHistoryEntry(6, "QA", "epam")
                        )),
                new Employee(
                        new Person("John", "Galt", 23),
                        Arrays.asList(
                                new JobHistoryEntry(3, "dev", "epam"),
                                new JobHistoryEntry(2, "dev", "google")
                        )),
                new Employee(
                        new Person("John", "Doe", 24),
                        Arrays.asList(
                                new JobHistoryEntry(4, "QA", "yandex"),
                                new JobHistoryEntry(2, "BA", "epam"),
                                new JobHistoryEntry(2, "dev", "abc")
                        )),
                new Employee(
                        new Person("John", "White", 25),
                        Collections.singletonList(
                                new JobHistoryEntry(6, "QA", "epam")
                        )),
                new Employee(
                        new Person("John", "Galt", 26),
                        Arrays.asList(
                                new JobHistoryEntry(3, "dev", "epam"),
                                new JobHistoryEntry(1, "dev", "google")
                        )),
                new Employee(
                        new Person("Bob", "Doe", 27),
                        Arrays.asList(
                                new JobHistoryEntry(4, "QA", "yandex"),
                                new JobHistoryEntry(2, "QA", "epam"),
                                new JobHistoryEntry(2, "dev", "abc")
                        )),
                new Employee(
                        new Person("John", "White", 28),
                        Collections.singletonList(
                                new JobHistoryEntry(6, "BA", "epam")
                        )),
                new Employee(
                        new Person("John", "Galt", 29),
                        Arrays.asList(
                                new JobHistoryEntry(3, "dev", "epam"),
                                new JobHistoryEntry(1, "dev", "google")
                        )),
                new Employee(
                        new Person("John", "Doe", 30),
                        Arrays.asList(
                                new JobHistoryEntry(4, "QA", "yandex"),
                                new JobHistoryEntry(2, "QA", "epam"),
                                new JobHistoryEntry(5, "dev", "abc")
                        )),
                new Employee(
                        new Person("Bob", "White", 31),
                        Collections.singletonList(
                                new JobHistoryEntry(6, "QA", "epam")
                        ))
        );
    }

}
