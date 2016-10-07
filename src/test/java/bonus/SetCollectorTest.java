package bonus;

import org.junit.Test;

import java.util.Set;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class SetCollectorTest {
    SetCollectorBenchmark scb = new SetCollectorBenchmark();

    @Test
    public void testSetCollection() {
        final Set<Integer> set1 = scb.collectSet();
        final Set<Integer> set2 = scb.collectConcurrentSet();
        assertThat(set1, is(set2));
    }


}
