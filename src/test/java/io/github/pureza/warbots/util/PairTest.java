package io.github.pureza.warbots.util;

import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class PairTest {

    @Test
    public void builderCreatesPairWithElementsInTheRightOrder() {
        Pair<String, Integer> pair = Pair.of("hello", 123);

        assertThat(pair.first(), is("hello"));
        assertThat(pair.second(), is(123));
    }
}
