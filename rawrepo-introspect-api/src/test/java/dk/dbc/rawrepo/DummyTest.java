package dk.dbc.rawrepo;

import org.junit.jupiter.api.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class DummyTest {

    @Test
    public void dummy() {
        assertThat(true, is(true));
    }
}
