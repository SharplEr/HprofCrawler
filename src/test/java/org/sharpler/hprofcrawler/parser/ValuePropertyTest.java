package org.sharpler.hprofcrawler.parser;

import com.pholser.junit.quickcheck.Property;
import com.pholser.junit.quickcheck.runner.JUnitQuickcheck;
import org.junit.jupiter.api.Assertions;
import org.junit.runner.RunWith;

@RunWith(JUnitQuickcheck.class)
public class ValuePropertyTest {

    @Property
    public void ofLong(long value) {
        var wrapped = Value.ofLong(value, false);
        Assertions.assertEquals(Type.LONG, wrapped.getType(), "wrong type");
        Assertions.assertEquals(value, wrapped.getLongValue(), "wrong value");
    }

    @Property
    public void ofInt(int value) {
        var wrapped = Value.ofInt(value);
        Assertions.assertEquals(Type.INT, wrapped.getType(), "wrong type");
        Assertions.assertEquals(value, wrapped.getIntValue(), "wrong value");
    }

    @Property
    public void ofShort(short value) {
        var wrapped = Value.ofShort(value);
        Assertions.assertEquals(Type.SHORT, wrapped.getType(), "wrong type");
        Assertions.assertEquals(value, wrapped.getShortValue(), "wrong value");
    }

    @Property
    public void ofChar(char value) {
        var wrapped = Value.ofChar(value);
        Assertions.assertEquals(Type.CHAR, wrapped.getType(), "wrong type");
        Assertions.assertEquals(value, wrapped.getCharValue(), "wrong value");
    }

    @Property
    public void ofByte(byte value) {
        var wrapped = Value.ofByte(value);
        Assertions.assertEquals(Type.BYTE, wrapped.getType(), "wrong type");
        Assertions.assertEquals(value, wrapped.getByteValue(), "wrong value");
        Assertions.assertSame(wrapped, Value.ofByte(value), "cache didn't work");
    }

    @Property
    public void ofBool(boolean value) {
        var wrapped = Value.ofBool(value);
        Assertions.assertEquals(Type.BOOL, wrapped.getType(), "wrong type");
        Assertions.assertEquals(value, wrapped.getBoolValue(), "wrong value");
        Assertions.assertSame(wrapped, Value.ofBool(value), "cache didn't work");
    }

    @Property
    public void ofDouble(double value) {
        var wrapped = Value.ofDouble(value);
        Assertions.assertEquals(Type.DOUBLE, wrapped.getType(), "wrong type");
        Assertions.assertEquals(value, wrapped.getDoublesValue(), "wrong value");
    }

    @Property
    public void ofFloat(float value) {
        var wrapped = Value.ofFloat(value);
        Assertions.assertEquals(Type.FLOAT, wrapped.getType(), "wrong type");
        Assertions.assertEquals(value, wrapped.getFloatValue(), "wrong value");
    }
}