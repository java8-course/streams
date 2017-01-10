package part3.exercise.lambda;

import data.Person;
import org.junit.Test;

import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

import static org.junit.Assert.assertEquals;

public class LambdaExercise {

    @Test
    public void supply() {
        final Person person = new Person("John", "Galt", 30);

        final Supplier<Person> getPerson = () -> person; // TODO return person from Supplier


        assertEquals(person, getPerson.get());
    }

    @Test
    public void function() {
        final Function<Person, String> getPersonName1 = person -> person.getFirstName(); // TODO get the name of person using expression lambda

        final Function<Person, String> getPersonName2 = Person::getFirstName; // TODO get the name of person using method reference

        // TODO get the name of person and log it to System.out using statement lambda: {}
        final Function<Person, String> getPersonNameAndLogIt = person -> {
            System.out.printf(person.getFirstName());
            return person.getFirstName();
        };

        final Person person = new Person("John", "Galt", 30);

        assertEquals(person.getFirstName(), getPersonName1.apply(person));
        assertEquals(person.getFirstName(), getPersonName2.apply(person));
        assertEquals(person.getFirstName(), getPersonNameAndLogIt.apply(person));
    }

    @Test
    public void combineFunctions() {
        final Function<Person, String> getPersonName = Person::getFirstName; // TODO get the name of person

        assertEquals("John", getPersonName.apply(new Person("John", "Galt", 30)));

        final Function<String, Integer> getStringLength = String::length; // TODO get string length

        assertEquals(Integer.valueOf(3), getStringLength.apply("ABC"));

        // TODO get person name length using getPersonName and getStringLength without andThen
        final Function<Person, Integer> getPersonNameLength1 = person -> getStringLength.apply(getPersonName.apply(person));

        // TODO get person name length using getPersonName and getStringLength with andThen
        final Function<Person, Integer> getPersonNameLength2 = getPersonName.andThen(getStringLength);

        final Person person = new Person("John", "Galt", 30);

        assertEquals(Integer.valueOf(4), getPersonNameLength1.apply(person));
        assertEquals(Integer.valueOf(4), getPersonNameLength2.apply(person));
    }

    private interface PersonFactory {
        Person create(String name, String lastName, int age);
    }

    private Person createPerson(PersonFactory pf) {
        return pf.create("John", "Galt", 66);
    }

    // ((T -> R), (R -> boolean)) -> (T -> boolean)
    private <T, R> Predicate<T> combine(Function<T, R> f, Predicate<R> p) {
        // TODO
//        throw new UnsupportedOperationException();
        return (T t) -> p.test(f.apply(t));
    }

    @Test
    public void methodReference() {
        // TODO use only method reverences here.
        final Person person = createPerson(Person::new); // TODO

        assertEquals(new Person("John", "Galt", 66), person);

        final Function<Person, String> getPersonName = Person::getFirstName; // TODO

        assertEquals("John", getPersonName.apply(person));

        final Predicate<String> isJohnString = "John"::equals; // TODO using method reference check that "John" equals string parameter

        final Predicate<Person> isJohnPerson = combine(getPersonName, isJohnString);

        assertEquals(true, isJohnPerson.test(person));
    }

}
