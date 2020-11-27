package org.sharpler.hrofcrawler.inspection;

import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.Test;

public class RangeShiftTest {
    @Test
    public void equalsTest() {
        EqualsVerifier.forClass(FindPrimArrayWithTooWideRange.RangeShift.class).verify();
    }
}
