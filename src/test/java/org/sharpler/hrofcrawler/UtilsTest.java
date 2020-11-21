package org.sharpler.hrofcrawler;

import com.pholser.junit.quickcheck.Property;
import com.pholser.junit.quickcheck.runner.JUnitQuickcheck;
import org.junit.jupiter.api.Assertions;
import org.junit.runner.RunWith;

@RunWith(JUnitQuickcheck.class)
public class UtilsTest {

    @Property
    public void serializeLong(long value) {
        Assertions.assertEquals(value, Utils.deserializeLong(Utils.serializeLong(value)));
    }
}