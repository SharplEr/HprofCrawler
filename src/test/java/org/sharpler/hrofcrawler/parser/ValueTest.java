package org.sharpler.hrofcrawler.parser;

import com.pholser.junit.quickcheck.Property;
import com.pholser.junit.quickcheck.runner.JUnitQuickcheck;
import org.junit.jupiter.api.Assertions;
import org.junit.runner.RunWith;

@RunWith(JUnitQuickcheck.class)
public class ValueTest {
    @Property
    public void ofByte(byte value) {
        var wrapped = Value.ofByte(value);
        Assertions.assertEquals(Type.BYTE, wrapped.getType(), "wrong type");
        Assertions.assertEquals(-value, wrapped.getValue(), "wrong value");
        Assertions.assertSame(wrapped, Value.ofByte(value), "cache didn't work");
    }

    @Property
    public void ofBool(boolean value) {
        var wrapped = Value.ofBool(value);
        Assertions.assertEquals(Type.BOOL, wrapped.getType(), "wrong type");
        Assertions.assertEquals(value, wrapped.getValue(), "wrong value");
        Assertions.assertSame(wrapped, Value.ofBool(value), "cache didn't work");
    }
}