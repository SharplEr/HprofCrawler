package org.sharpler.hprofcrawler.parser;

import java.nio.ByteBuffer;

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

    byte[] serialize();

    static Value deserialize(ByteBuffer buffer) {
        return switch (Type.VALUES.get(buffer.get())) {
            case OBJ -> ofLong(buffer.getLong(), true);
            case BOOL -> ofBool(buffer.get() == 1);
            case CHAR -> ofChar(buffer.getChar());
            case FLOAT -> ofFloat(buffer.getFloat());
            case DOUBLE -> ofDouble(buffer.getDouble());
            case BYTE -> ByteValue.of(buffer.get());
            case SHORT -> ofShort(buffer.getShort());
            case INT -> ofInt(buffer.getInt());
            case LONG -> ofLong(buffer.getLong(), false);
        };
    }


    final class LongValue implements Value {
        private final long value;
        private final boolean isObject;

        LongValue(long value, boolean isObject) {
            this.value = value;
            this.isObject = isObject;
        }

        @Override
        public Type getType() {
            return isObject ? Type.OBJ : Type.LONG;
        }

        @Override
        public byte[] serialize() {
            var buffer = ByteBuffer.allocate(1 + Long.BYTES);
            buffer.put((byte) getType().ordinal());
            buffer.putLong(value);
            return buffer.array();
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
        private final int value;

        IntValue(int value) {
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
        public byte[] serialize() {
            var buffer = ByteBuffer.allocate(1 + Integer.BYTES);
            buffer.put((byte) getType().ordinal());
            buffer.putInt(value);
            return buffer.array();
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
        private final short value;

        ShortValue(short value) {
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
        public byte[] serialize() {
            var buffer = ByteBuffer.allocate(1 + Short.BYTES);
            buffer.put((byte) getType().ordinal());
            buffer.putShort(value);
            return buffer.array();
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
        private final char value;

        CharValue(char value) {
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
        public byte[] serialize() {
            var buffer = ByteBuffer.allocate(1 + Character.BYTES);
            buffer.put((byte) getType().ordinal());
            buffer.putChar(value);
            return buffer.array();
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

        private final byte value;

        private ByteValue(byte value) {
            this.value = value;
        }

        static ByteValue of(byte value) {
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
        public byte[] serialize() {
            var buffer = ByteBuffer.allocate(1 + Byte.BYTES);
            buffer.put((byte) getType().ordinal());
            buffer.put(value);
            return buffer.array();
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
        static final BooleanValue TRUE = new BooleanValue(true);
        static final BooleanValue FALSE = new BooleanValue(false);

        private final boolean value;

        private BooleanValue(boolean value) {
            this.value = value;
        }

        static BooleanValue of(boolean value) {
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
        public byte[] serialize() {
            var buffer = ByteBuffer.allocate(1 + 1);
            buffer.put((byte) getType().ordinal());
            buffer.put(value ? (byte) 1 : (byte) 0);
            return buffer.array();
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
        private final double value;

        DoubleValue(double value) {
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
        public byte[] serialize() {
            var buffer = ByteBuffer.allocate(1 + Double.BYTES);
            buffer.put((byte) getType().ordinal());
            buffer.putDouble(value);
            return buffer.array();
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
        private final float value;

        FloatValue(float value) {
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
        public byte[] serialize() {
            var buffer = ByteBuffer.allocate(1 + Float.BYTES);
            buffer.put((byte) getType().ordinal());
            buffer.putFloat(value);
            return buffer.array();
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
            return (value == +0.0f ? 0 : Float.floatToIntBits(value));
        }

        @Override
        public String toString() {
            return "FloatValue{" +
                    "value=" + value +
                    '}';
        }
    }
}
