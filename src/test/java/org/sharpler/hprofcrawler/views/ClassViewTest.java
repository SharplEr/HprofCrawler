package org.sharpler.hprofcrawler.views;

import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.jupiter.api.Test;

public class ClassViewTest {

    @Test
    public void testEquals() {
        EqualsVerifier.forClass(ClassView.class)
                .withOnlyTheseFields("id")
                .verify();
    }
}