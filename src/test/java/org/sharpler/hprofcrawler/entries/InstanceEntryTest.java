package org.sharpler.hprofcrawler.entries;

import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.Test;

public class InstanceEntryTest {

    @Test
    public void equalsContract() {
        EqualsVerifier.forClass(InstanceEntry.class).verify();
    }
}