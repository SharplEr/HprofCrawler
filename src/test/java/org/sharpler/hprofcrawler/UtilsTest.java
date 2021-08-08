package org.sharpler.hprofcrawler;

import com.pholser.junit.quickcheck.Property;
import com.pholser.junit.quickcheck.runner.JUnitQuickcheck;
import org.junit.jupiter.api.Assertions;
import org.junit.runner.RunWith;

import java.util.function.Supplier;

@RunWith(JUnitQuickcheck.class)
public class UtilsTest {

    @Property
    public void serializeLong(long value) {
        Assertions.assertEquals(value, Utils.deserializeLong(Utils.serializeLong(value)));
    }
}