package org.sharpler.hrofcrawler.entries;

import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.Test;

public class FieldEntryTest {

    @Test
    public void equalsContract() {
        EqualsVerifier.forClass(FieldEntry.class).verify();
    }
}