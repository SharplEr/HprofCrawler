package org.sharpler.hprofcrawler.parser;

import org.sharpler.hprofcrawler.Utils;

import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.StringJoiner;
import java.util.function.Function;

public class PrimArray {
    private final Type type;
    private final long objectId;
    private final Object array;

    PrimArray(Type type, long objectId, Object array) {
        this.objectId = objectId;
        this.type = type;
        this.array = array;

        switch (type) {
            case OBJ:
                throw new IllegalStateException("should be not object");
            case LONG:
                assert array instanceof long[];
                break;
            case BOOL:
                assert array instanceof boolean[];
                break;
            case CHAR:
                assert array instanceof char[];
                break;
            case FLOAT:
                assert array instanceof float[];
                break;
            case DOUBLE:
                assert array instanceof double[];
                break;
            case BYTE:
                assert array instanceof byte[];
                break;
            case SHORT:
                assert array instanceof short[];
                break;
            case INT:
                assert array instanceof int[];
                break;
        }
    }

    public final Type getType() {
        return type;
    }

    public final Object getArrayRaw() {
        return array;
    }

    public final long getObjectId() {
        return objectId;
    }

    public final int getLength() {
        return switch (type) {
            case LONG -> ((long[]) array).length;
            case BOOL -> ((boolean[]) array).length;
            case CHAR -> ((char[]) array).length;
            case FLOAT -> ((float[]) array).length;
            case DOUBLE -> ((double[]) array).length;
            case BYTE -> ((byte[]) array).length;
            case SHORT -> ((short[]) array).length;
            case INT -> ((int[]) array).length;
            case OBJ -> throw new IllegalStateException("Objects are not supported");
        };
    }

    public final <T> T map(
            Function<long[], ? extends T> forLong,
            Function<int[], ? extends T> forInt,
            Function<short[], ? extends T> forShort,
            Function<byte[], ? extends T> forByte,
            Function<boolean[], ? extends T> forBoolean,
            Function<char[], ? extends T> forChar,
            Function<double[], ? extends T> forDouble,
            Function<float[], ? extends T> forFloat
    ) {
        return switch (type) {
            case LONG -> forLong.apply((long[]) array);
            case BOOL -> forBoolean.apply((boolean[]) array);
            case CHAR -> forChar.apply((char[]) array);
            case FLOAT -> forFloat.apply((float[]) array);
            case DOUBLE -> forDouble.apply((double[]) array);
            case BYTE -> forByte.apply((byte[]) array);
            case SHORT -> forShort.apply((short[]) array);
            case INT -> forInt.apply((int[]) array);
            case OBJ -> throw new IllegalStateException("Objects are not supported");
        };
    }

    public final byte[] serialize() {
        int length = getLength();

        ByteBuffer buffer = ByteBuffer.allocate(Integer.BYTES + Long.BYTES + type.sizeInBytes() * length);

        buffer.putInt(type.ordinal());
        buffer.putLong(objectId);
        switch (type) {
            case LONG -> {
                for (long t : (long[]) array) {
                    buffer.putLong(t);
                }
            }
            case BOOL -> {
                for (boolean t : (boolean[]) array) {
                    buffer.put(t ? (byte) 1 : (byte) 0);
                }
            }
            case CHAR -> {
                for (char t : (char[]) array) {
                    buffer.putChar(t);
                }
            }
            case FLOAT -> {
                for (float t : (float[]) array) {
                    buffer.putFloat(t);
                }
            }
            case DOUBLE -> {
                for (double t : (double[]) array) {
                    buffer.putDouble(t);
                }
            }
            case BYTE -> buffer.put((byte[]) array);
            case SHORT -> {
                for (short t : (short[]) array) {
                    buffer.putShort(t);
                }
            }
            case INT -> {
                for (int t : (int[]) array) {
                    buffer.putInt(t);
                }
            }
            case OBJ -> throw new IllegalStateException("Objects are not supported");
        }

        return buffer.array();
    }

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PrimArray)) return false;

        PrimArray primArray = (PrimArray) o;

        if (objectId != primArray.objectId) return false;
        if (type != primArray.type) return false;

        return map(
                x -> Arrays.equals(x, (long[]) primArray.array),
                x -> Arrays.equals(x, (int[]) primArray.array),
                x -> Arrays.equals(x, (short[]) primArray.array),
                x -> Arrays.equals(x, (byte[]) primArray.array),
                x -> Arrays.equals(x, (boolean[]) primArray.array),
                x -> Arrays.equals(x, (char[]) primArray.array),
                x -> Arrays.equals(x, (double[]) primArray.array),
                x -> Arrays.equals(x, (float[]) primArray.array)
        );
    }

    @Override
    public final int hashCode() {
        int result = type.hashCode();
        result = 31 * result + (int) (objectId ^ (objectId >>> 32));
        result = 31 * result + array.hashCode();
        return result;
    }

    @Override
    public final String toString() {
        return new StringJoiner(", ", PrimArray.class.getSimpleName() + "[", "]")
                .add("type=" + type)
                .add("objectId=" + objectId)
                .add("array=" + array)
                .toString();
    }

    public static PrimArray deserialize(byte[] data) {
        ByteBuffer buffer = ByteBuffer.wrap(data);

        Type type = Type.VALUES.get(buffer.getInt());
        long objectId = buffer.getLong();

        int length = (data.length - Integer.BYTES - Long.BYTES) / type.sizeInBytes();

        switch (type) {
            case LONG:
                long[] longs = new long[length];
                for (int i = 0; i < length; i++) {
                    longs[i] = buffer.getLong();
                }

                return ofLongs(longs, objectId);
            case BOOL:
                boolean[] bools = new boolean[length];
                for (int i = 0; i < length; i++) {
                    bools[i] = buffer.get() == 1;
                }

                return ofBooleans(bools, objectId);
            case CHAR:
                char[] chars = new char[length];
                for (int i = 0; i < length; i++) {
                    chars[i] = buffer.getChar();
                }

                return ofChars(chars, objectId);
            case FLOAT:
                float[] floats = new float[length];
                for (int i = 0; i < length; i++) {
                    floats[i] = buffer.getFloat();
                }

                return ofFloats(floats, objectId);
            case DOUBLE:
                double[] doubles = new double[length];
                for (int i = 0; i < length; i++) {
                    doubles[i] = buffer.getDouble();
                }

                return ofDoubles(doubles, objectId);
            case BYTE:
                byte[] bytes = new byte[length];
                for (int i = 0; i < length; i++) {
                    bytes[i] = buffer.get();
                }

                return ofBytes(bytes, objectId);
            case SHORT:
                short[] shorts = new short[length];
                for (int i = 0; i < length; i++) {
                    shorts[i] = buffer.getShort();
                }

                return ofShorts(shorts, objectId);
            case INT:
                int[] ints = new int[length];
                for (int i = 0; i < length; i++) {
                    ints[i] = buffer.getInt();
                }

                return ofInts(ints, objectId);
        }
        throw new IllegalStateException();
    }

    public static PrimArray ofLongs(long[] array, long objectId) {
        return new PrimArray(Type.LONG, objectId, array);
    }

    public static PrimArray ofInts(int[] array, long objectId) {
        return new PrimArray(Type.INT, objectId, array);
    }

    public static PrimArray ofShorts(short[] array, long objectId) {
        return new PrimArray(Type.SHORT, objectId, array);
    }

    public static PrimArray ofBytes(byte[] array, long objectId) {
        return new PrimArray(Type.BYTE, objectId, array);
    }

    public static PrimArray ofBooleans(boolean[] array, long objectId) {
        return new PrimArray(Type.BOOL, objectId, array);
    }

    public static PrimArray ofChars(char[] array, long objectId) {
        return new PrimArray(Type.CHAR, objectId, array);
    }

    public static PrimArray ofDoubles(double[] array, long objectId) {
        return new PrimArray(Type.DOUBLE, objectId, array);
    }

    public static PrimArray ofFloats(float[] array, long objectId) {
        return new PrimArray(Type.FLOAT, objectId, array);
    }
}

