package org.sharpler.hprofcrawler.entries;

import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.Test;

public class ClassEntryTest {
    @Test
    public void equalsContract() {
        EqualsVerifier.forClass(ClassEntry.class).verify();
    }
}
