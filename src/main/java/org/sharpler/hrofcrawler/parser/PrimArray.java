package org.sharpler.hrofcrawler.parser;

import org.sharpler.hrofcrawler.Utils;

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
        assert array != null;
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

    public Type getType() {
        return type;
    }

    public Object getArrayRaw() {
        return array;
    }

    public long getObjectId() {
        return objectId;
    }

    public int getLength() {
        switch (type) {
            case LONG:
                return ((long[]) array).length;
            case BOOL:
                return ((boolean[]) array).length;
            case CHAR:
                return ((char[]) array).length;
            case FLOAT:
                return ((float[]) array).length;
            case DOUBLE:
                return ((double[]) array).length;
            case BYTE:
                return ((byte[]) array).length;
            case SHORT:
                return ((short[]) array).length;
            case INT:
                return ((int[]) array).length;
        }

        throw new IllegalStateException();
    }

    public <T> T map(
            Function<long[], ? extends T> forLong,
            Function<int[], ? extends T> forInt,
            Function<short[], ? extends T> forShort,
            Function<byte[], ? extends T> forByte,
            Function<boolean[], ? extends T> forBoolean,
            Function<char[], ? extends T> forChar,
            Function<double[], ? extends T> forDouble,
            Function<float[], ? extends T> forFloat
    )
    {
        switch (type) {
            case LONG:
                return forLong.apply((long[]) array);
            case BOOL:
                return forBoolean.apply((boolean[]) array);
            case CHAR:
                return forChar.apply((char[]) array);
            case FLOAT:
                return forFloat.apply((float[]) array);
            case DOUBLE:
                return forDouble.apply((double[]) array);
            case BYTE:
                return forByte.apply((byte[]) array);
            case SHORT:
                return forShort.apply((short[]) array);
            case INT:
                return forInt.apply((int[]) array);
        }

        throw new IllegalStateException();
    }

    public byte[] serialize() {
        int length = getLength();

        ByteBuffer buffer = ByteBuffer.allocate(
                1 * Integer.BYTES +
                        1 * Long.BYTES +
                        type.sizeInBytes() * length
        );

        buffer.putInt(type.ordinal());
        buffer.putLong(objectId);

        map(
                x -> {
                    for (long t : x) {
                        buffer.putLong(t);
                    }
                    return Utils.nullTs();
                },
                x -> {
                    for (int t : x) {
                        buffer.putInt(t);
                    }
                    return Utils.nullTs();
                },
                x -> {
                    for (short t : x) {
                        buffer.putShort(t);
                    }
                    return Utils.nullTs();
                },
                x -> {
                    for (byte t : x) {
                        buffer.put(t);
                    }
                    return Utils.nullTs();
                },
                x -> {
                    for (boolean t : x) {
                        buffer.put(t ? (byte) 1 : (byte) 0);
                    }
                    return Utils.nullTs();
                },
                x -> {
                    for (char t : x) {
                        buffer.putChar(t);
                    }
                    return Utils.nullTs();
                },
                x -> {
                    for (double t : x) {
                        buffer.putDouble(t);
                    }
                    return Utils.nullTs();
                },
                x -> {
                    for (float t : x) {
                        buffer.putFloat(t);
                    }
                    return Utils.nullTs();
                }
        );

        return buffer.array();
    }

    @Override
    public boolean equals(Object o) {
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
    public int hashCode() {
        int result = type.hashCode();
        result = 31 * result + (int) (objectId ^ (objectId >>> 32));
        result = 31 * result + array.hashCode();
        return result;
    }

    @Override
    public String toString() {
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

