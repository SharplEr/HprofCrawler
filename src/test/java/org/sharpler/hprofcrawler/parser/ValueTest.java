package org.sharpler.hprofcrawler.parser;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.jupiter.api.Test;

import java.util.stream.Stream;

public class ValueTest {
    @Test
    public void testEquals() {
        EqualsVerifier.forClass(Value.class).verify();
    }
}
