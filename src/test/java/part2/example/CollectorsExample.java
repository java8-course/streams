package part2.example;

import data.Person;
import org.junit.Test;

import java.util.Collections;
import java.util.EnumSet;
import java.util.Set;
import java.util.StringJoiner;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.Assert.assertEquals;

public class CollectorsExample {

    private static Stream<Person> getPersonStream() {
        return Stream.of(
                new Person("a", "a", 1),
                new Person("b", "b", 2),
                new Person("c", "c", 3),
                new Person("d", "d", 4),
                new Person("e", "e", 5)
        );
    }

    @Test
    public void incorrectReducePersonsToString() {
        final Stream<Person> personStream = getPersonStream();

        // a + (b + c) == (a + b) + c
        // [a, b, c, d] -> [[a, b], [c, d]]
        final String result = personStream//.parallel()
                // .reduce((a, b) -> ???) Optional<T> reduce(BinaryOperator<T> accumulator)
                // .reduce(zeroPerson, (a, b) -> ???) T reduce(T identity, BinaryOperator<T> accumulator)
                //     <U> U reduce(U identity,
                //                  BiFunction<U, ? super T, U> accumulator,
                //                  BinaryOperator<U> combiner)
                .reduce( // not constrained to execute sequentially
                        "",
                        (accum, person) -> accum + "\n and " + person.toString(),
                        (accum1, accum2) -> accum1 + "\n and " + accum2);

        System.out.println(result);
    }

    @Test
    public void incorrectCollectPersonsToString() {
        final StringBuilder res = getPersonStream().parallel()
                //.unordered()
                .collect(
                        StringBuilder::new, // synchronization is in Stream
                        (builder, person) -> builder.append("\n and ").append(person),
                        (builder1, builder2) -> builder1.append("\n and ").append(builder2)
                );

        final String result = res.toString();

        System.out.println(result);
    }

    @Test
    public void collectPersonToString1() {
        final StringJoiner res = getPersonStream().parallel()
                .collect(
                        () -> new StringJoiner("\n and "), // synchronization is in Stream
                        (joiner, person) -> joiner.add(person.toString()),
                        (joiner1, joiner2) -> joiner1.merge(joiner2)
                );

        final String result = res.toString();

        System.out.println(result);
    }

    @Test
    public void collectPersonToString2() {
        final String result = getPersonStream().parallel()
                .collect(
                        new Collector<Person, StringJoiner, String>() {
                            @Override
                            public Supplier<StringJoiner> supplier() {
                                return () -> new StringJoiner("\n and ");
                            }

                            @Override
                            public BiConsumer<StringJoiner, Person> accumulator() {
                                return (sj, p) -> sj.add(p.toString());
                            }

                            @Override
                            public BinaryOperator<StringJoiner> combiner() {
                                return StringJoiner::merge;
                            }

                            @Override
                            public Function<StringJoiner, String> finisher() {
                                return StringJoiner::toString;
                            }

                            @Override
                            public Set<Characteristics> characteristics() {
                                //return Collections.emptySet();
                                return Collections.emptySet();
                            }
                        }
                );

        System.out.println(result);
    }

    @Test
    public void collectPersonToString3() {
        final String expected = getPersonStream().parallel()
                .map(Object::toString)
                .collect(Collectors.joining("\n and "));

        final String result = getPersonStream().parallel()
                .collect(
                        Collectors.mapping(
                                Object::toString,
                                Collectors.joining("\n and "))
                );


        System.out.println(result);

        assertEquals(expected, result);
    }


}
