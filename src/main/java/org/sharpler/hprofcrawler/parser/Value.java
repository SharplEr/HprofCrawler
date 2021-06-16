package org.sharpler.hprofcrawler.parser;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = Value.LongValue.class, name = "long"),
        @JsonSubTypes.Type(value = Value.IntValue.class, name = "int"),
        @JsonSubTypes.Type(value = Value.ShortValue.class, name = "short"),
        @JsonSubTypes.Type(value = Value.ByteValue.class, name = "byte"),
        @JsonSubTypes.Type(value = Value.CharValue.class, name = "char"),
        @JsonSubTypes.Type(value = Value.BooleanValue.class, name = "bool"),
        @JsonSubTypes.Type(value = Value.DoubleValue.class, name = "double"),
        @JsonSubTypes.Type(value = Value.FloatValue.class, name = "float")
})
public interface Value {
    static LongValue ofLong(long value, boolean isObject) {
        return new LongValue(value, isObject);
    }

    static IntValue ofInt(int value) {
        return new IntValue(value);
    }

    static ShortValue ofShort(short value) {
        return new ShortValue(value);
    }

    static CharValue ofChar(char value) {
        return new CharValue(value);
    }

    static ByteValue ofByte(byte value) {
        return ByteValue.of(value);
    }

    static BooleanValue ofBool(boolean value) {
        return BooleanValue.of(value);
    }

    static DoubleValue ofDouble(double value) {
        return new DoubleValue(value);
    }

    static FloatValue ofFloat(float value) {
        return new FloatValue(value);
    }

    Type getType();

    final class LongValue implements Value {
        @JsonProperty("value")
        private final long value;
        @JsonProperty("isObject")
        private final boolean isObject;

        @JsonCreator
        public LongValue(@JsonProperty("value") long value, @JsonProperty("isObject") boolean isObject) {
            this.value = value;
            this.isObject = isObject;
        }

        @Override
        public Type getType() {
            return isObject ? Type.OBJ : Type.LONG;
        }

        public long getValue() {
            return value;
        }

        @Override
        public String toString() {
            return "LongValue{" +
                    "value=" + value +
                    ", isObject=" + isObject +
                    '}';
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof LongValue)) return false;

            LongValue longValue = (LongValue) o;

            if (value != longValue.value) return false;
            return isObject == longValue.isObject;
        }

        @Override
        public int hashCode() {
            int result = (int) (value ^ (value >>> 32));
            result = 31 * result + (isObject ? 1 : 0);
            return result;
        }
    }

    final class IntValue implements Value {
        @JsonProperty("value")
        private final int value;

        @JsonCreator
        public IntValue(@JsonProperty("value") int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }


        @Override
        public Type getType() {
            return Type.INT;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof IntValue)) return false;

            IntValue intValue = (IntValue) o;

            return value == intValue.value;
        }

        @Override
        public int hashCode() {
            return value;
        }

        @Override
        public String toString() {
            return "IntValue{" +
                    "value=" + value +
                    '}';
        }
    }

    final class ShortValue implements Value {
        @JsonProperty("value")
        private final short value;

        @JsonCreator
        public ShortValue(@JsonProperty("value") short value) {
            this.value = value;
        }

        public short getValue() {
            return value;
        }

        @Override
        public Type getType() {
            return Type.SHORT;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof ShortValue)) return false;

            ShortValue that = (ShortValue) o;

            return value == that.value;
        }

        @Override
        public int hashCode() {
            return value;
        }

        @Override
        public String toString() {
            return "ShortValue{" +
                    "value=" + value +
                    '}';
        }
    }

    final class CharValue implements Value {
        @JsonProperty("value")
        private final char value;

        @JsonCreator
        public CharValue(@JsonProperty("value") char value) {
            this.value = value;
        }

        public char getValue() {
            return value;
        }

        @Override
        public Type getType() {
            return Type.CHAR;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof CharValue)) return false;

            CharValue charValue = (CharValue) o;

            return value == charValue.value;
        }

        @Override
        public int hashCode() {
            return value;
        }

        @Override
        public String toString() {
            return "CharValue{" +
                    "value=" + value +
                    '}';
        }
    }

    final class ByteValue implements Value {
        private static final ByteValue[] cache = new ByteValue[256];

        static {
            for (int i = 0; i < 256; i++) {
                cache[i] = new ByteValue((byte) (i - 128));
            }
        }

        @JsonProperty("value")
        private final byte value;

        private ByteValue(byte value) {
            this.value = value;
        }

        @JsonCreator
        public static ByteValue of(@JsonProperty("value") byte value) {
            return cache[value + 128];
        }

        public byte getValue() {
            return value;
        }

        @Override
        public Type getType() {
            return Type.BYTE;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof ByteValue)) return false;

            ByteValue byteValue = (ByteValue) o;

            return value == byteValue.value;
        }

        @Override
        public int hashCode() {
            return value;
        }

        @Override
        public String toString() {
            return "ByteValue{" +
                    "value=" + value +
                    '}';
        }
    }

    final class BooleanValue implements Value {
        public static final BooleanValue TRUE = new BooleanValue(true);
        public static final BooleanValue FALSE = new BooleanValue(false);

        @JsonProperty("value")
        private final boolean value;

        private BooleanValue(boolean value) {
            this.value = value;
        }

        @JsonCreator
        public static BooleanValue of(@JsonProperty("value") boolean value) {
            return value ? TRUE : FALSE;
        }

        public boolean getValue() {
            return value;
        }

        @Override
        public Type getType() {
            return Type.BOOL;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof BooleanValue)) return false;

            BooleanValue that = (BooleanValue) o;

            return value == that.value;
        }

        @Override
        public int hashCode() {
            return (value ? 1 : 0);
        }

        @Override
        public String toString() {
            return "BooleanValue{" +
                    "value=" + value +
                    '}';
        }
    }

    final class DoubleValue implements Value {
        @JsonProperty("value")
        private final double value;

        @JsonCreator
        public DoubleValue(@JsonProperty("value") double value) {
            this.value = value;
        }

        public double getValue() {
            return value;
        }

        @Override
        public Type getType() {
            return Type.DOUBLE;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof DoubleValue)) return false;

            DoubleValue that = (DoubleValue) o;

            return Double.compare(that.value, value) == 0;
        }

        @Override
        public int hashCode() {
            long temp = Double.doubleToLongBits(value);
            return (int) (temp ^ (temp >>> 32));
        }

        @Override
        public String toString() {
            return "DoubleValue{" +
                    "value=" + value +
                    '}';
        }
    }

    final class FloatValue implements Value {
        @JsonProperty("value")
        private final float value;

        @JsonCreator
        public FloatValue(@JsonProperty("value") float value) {
            this.value = value;
        }

        public float getValue() {
            return value;
        }

        @Override
        public Type getType() {
            return Type.FLOAT;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof FloatValue)) return false;

            FloatValue that = (FloatValue) o;

            return Float.compare(that.value, value) == 0;
        }

        @Override
        public int hashCode() {
            return (value != +0.0f ? Float.floatToIntBits(value) : 0);
        }

        @Override
        public String toString() {
            return "FloatValue{" +
                    "value=" + value +
                    '}';
        }
    }
}
