package org.sharpler.hprofcrawler.parser;

import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.jupiter.api.Test;

public class ValueTest {
    @Test
    public void testEquals() {
        EqualsVerifier.forClass(Value.class).verify();
    }
}
