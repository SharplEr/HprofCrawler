package org.sharpler.hrofcrawler.entries;

import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.Test;

public class InstanceEntryTest {

    @Test
    public void equalsContract() {
        EqualsVerifier.forClass(InstanceEntry.class).verify();
    }
}