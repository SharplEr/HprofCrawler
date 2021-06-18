package org.sharpler.hprofcrawler.parser;

import java.nio.ByteBuffer;

public final class Value {
    private final Type type;
    private final long value;

    private Value(Type type, long value) {
        this.type = type;
        this.value = value;
    }

    private static final Value DOUBLE_ZERO = new Value(Type.DOUBLE, Double.doubleToLongBits(0f));
    private static final Value DOUBLE_ONE = new Value(Type.DOUBLE, Double.doubleToLongBits(1f));

    public static Value ofDouble(double v) {
        if (v == 0d) {
            return DOUBLE_ZERO;
        }
        if (v == 1d) {
            return DOUBLE_ONE;
        }
        return new Value(Type.DOUBLE, Double.doubleToLongBits(v));
    }

    private static final Value FLOAT_ZERO = new Value(Type.FLOAT, Float.floatToIntBits(0f));
    private static final Value FLOAT_ONE = new Value(Type.FLOAT, Float.floatToIntBits(1f));

    public static Value ofFloat(float v) {
        if (v == 0f) {
            return FLOAT_ZERO;
        }
        if (v == 1f) {
            return FLOAT_ONE;
        }

        return new Value(Type.FLOAT, Float.floatToIntBits(v));
    }

    private static final Value[] LONG_CACHE = new Value[4096];

    static {
        for (int i = 0; i < LONG_CACHE.length; i++) {
            LONG_CACHE[i] = new Value(Type.LONG, i);
        }
    }

    public static Value ofLong(long v, boolean isObject) {
        if (isObject) {
            return new Value(Type.OBJ, v);
        }
        if (v >= 0 && v < LONG_CACHE.length) {
            return LONG_CACHE[(int) v];
        }
        return new Value(Type.LONG, v);
    }

    private static final Value[] INT_CACHE = new Value[4096];

    static {
        for (int i = 0; i < INT_CACHE.length; i++) {
            INT_CACHE[i] = new Value(Type.INT, i);
        }
    }

    public static Value ofInt(int v) {
        if (v >= 0 && v < INT_CACHE.length) {
            return INT_CACHE[v];
        }
        return new Value(Type.INT, v);
    }

    private static final Value[] SHORT_CACHE = new Value[4096];

    static {
        for (int i = 0; i < SHORT_CACHE.length; i++) {
            SHORT_CACHE[i] = new Value(Type.SHORT, i);
        }
    }

    public static Value ofShort(short v) {
        if (v >= 0 && v < SHORT_CACHE.length) {
            return SHORT_CACHE[v];
        }
        return new Value(Type.SHORT, v);
    }

    private static final Value[] BYTE_CACHE = new Value[256];

    static {
        for (int i = 0; i < 256; i++) {
            BYTE_CACHE[i] = new Value(Type.BYTE, i - 128);
        }
    }

    public static Value ofByte(byte v) {
        return BYTE_CACHE[v + 128];
    }

    private static final Value[] CHAR_CACHE = new Value[256];

    static {
        for (int i = 0; i < CHAR_CACHE.length; i++) {
            CHAR_CACHE[i] = new Value(Type.CHAR, i);
        }
    }

    public static Value ofChar(char v) {
        if (v < CHAR_CACHE.length) {
            return CHAR_CACHE[v];
        }
        return new Value(Type.CHAR, v);
    }

    private static final Value TRUE = new Value(Type.BOOL, 1);
    private static final Value FALSE = new Value(Type.BOOL, 0);

    public static Value ofBool(boolean v) {
        return v ? TRUE : FALSE;
    }

    public Type getType() {
        return type;
    }

    public byte[] serialize() {
        return new byte[]{
                (byte) type.ordinal(),
                (byte) ((value >> 56) & 0xff),
                (byte) ((value >> 48) & 0xff),
                (byte) ((value >> 40) & 0xff),
                (byte) ((value >> 32) & 0xff),
                (byte) ((value >> 24) & 0xff),
                (byte) ((value >> 16) & 0xff),
                (byte) ((value >> 8) & 0xff),
                (byte) ((value) & 0xff),
        };
    }

    public static Value deserialize(ByteBuffer buffer) {
        return new Value(
                Type.VALUES.get(buffer.get()),
                buffer.getLong()
        );
    }

    public long getLongValue() {
        assert type == Type.LONG || type == Type.OBJ;
        return value;
    }

    public int getIntValue() {
        assert type == Type.INT;
        return (int) value;
    }

    public short getShortValue() {
        assert type == Type.SHORT;
        return (short) value;
    }

    public char getCharValue() {
        assert type == Type.CHAR;
        return (char) value;
    }

    public byte getByteValue() {
        assert type == Type.BYTE;
        return (byte) value;
    }

    public boolean getBoolValue() {
        assert type == Type.BOOL;
        return value != 0L;
    }

    public float getFloatValue() {
        assert type == Type.FLOAT;
        return Float.intBitsToFloat((int) value);
    }

    public double getDoublesValue() {
        assert type == Type.DOUBLE;
        return Double.longBitsToDouble(value);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Value value = (Value) o;

        if (this.value != value.value) return false;
        return type == value.type;
    }

    @Override
    public int hashCode() {
        int result = type.hashCode();
        result = 31 * result + (int) (value ^ (value >>> 32));
        return result;
    }

    @Override
    public String toString() {
        return "Value2{" +
                "type=" + type +
                ", value=" + value +
                '}';
    }
}
