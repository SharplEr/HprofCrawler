package org.sharpler.hprofcrawler.parser;

import java.nio.ByteBuffer;

public class ObjectArray {
    private final long objectId;
    private final long elementsClassId;
    private final long[] values;

    public ObjectArray(long objectId, long elementsClassId, long[] values) {
        this.objectId = objectId;
        this.elementsClassId = elementsClassId;
        this.values = values;
    }

    public final long getObjectId() {
        return objectId;
    }

    public final long getElementsClassId() {
        return elementsClassId;
    }

    public final long[] getValues() {
        return values;
    }

    public final byte[] serialize() {
        ByteBuffer buffer = ByteBuffer.allocate(2 * Long.BYTES + Long.BYTES * values.length);

        buffer.putLong(elementsClassId);
        buffer.putLong(objectId);
        for (long x : values) {
            buffer.putLong(x);
        }

        return buffer.array();
    }

    public static ObjectArray deserialize(byte[] data) {
        ByteBuffer buffer = ByteBuffer.wrap(data);

        long elementsClassId = buffer.getLong();
        long objectId = buffer.getLong();

        int length = (data.length - (2 * Long.BYTES)) / Long.BYTES;

        long[] values = new long[length];

        for (int i = 0; i < length; i++) {
            values[i] = buffer.getLong();
        }

        return new ObjectArray(objectId, elementsClassId, values);
    }
}

