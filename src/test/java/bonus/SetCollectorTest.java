package bonus;

import org.junit.Test;

import java.util.Set;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class SetCollectorTest {
    private SetCollectorBenchmark scb = new SetCollectorBenchmark(10_000);

    @Test
    public void testSetCollection() {
        final Set<String> set0 = scb.collectNonConcurrentSet();
        final Set<String> set1 = scb.collectConcurrentSet();
        final Set<String> set2 = scb.collectConcurrentSetSingleAccum();
        assertThat(set1, is(set0));
        assertThat(set1, is(set2));
    }


}
