package org.sharpler.hrofcrawler.parser;

import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.jupiter.api.Test;

class CPUSampleTest {
    @Test
    public void equalsContract() {
        EqualsVerifier.forClass(CPUSample.class).verify();
    }
}