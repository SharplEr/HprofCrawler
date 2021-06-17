package org.sharpler.hprofcrawler.entries;

import it.unimi.dsi.fastutil.bytes.ByteArrayList;
import org.sharpler.hprofcrawler.Utils;
import org.sharpler.hprofcrawler.parser.Value;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

public final class InstanceEntry {
    private final long objectId;
    private final long classId;
    private final List<Value> fields;

    public InstanceEntry(long objectId, long classId, List<Value> fields) {
        this.objectId = objectId;
        this.classId = classId;
        this.fields = fields;
    }

    public long getObjectId() {
        return objectId;
    }

    public long getClassId() {
        return classId;
    }

    public List<Value> getFields() {
        return fields;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof InstanceEntry)) return false;

        InstanceEntry entry = (InstanceEntry) o;

        if (objectId != entry.objectId) return false;
        if (classId != entry.classId) return false;
        if (!fields.equals(entry.fields)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = (int) (objectId ^ (objectId >>> 32));
        result = 31 * result + (int) (classId ^ (classId >>> 32));
        result = 31 * result + fields.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "InstanceEntry{" +
                "objectId=" + objectId +
                ", classId=" + classId +
                ", fields=" + fields +
                '}';
    }

    private static void addAll(ByteArrayList list, byte[] bytes) {
        for (var x : bytes) {
            list.add(x);
        }
    }

    public byte[] serialize() {
        var result = new ByteArrayList();
        addAll(result, Utils.serializeLong(objectId));
        addAll(result, Utils.serializeLong(classId));
        for (var field : fields) {
            addAll(result, field.serialize());
        }
        return result.toByteArray();
    }

    public static InstanceEntry deserialize(byte[] data) {
        var buffer = ByteBuffer.wrap(data);
        var objectId = buffer.getLong();
        var classId = buffer.getLong();
        var fields = new ArrayList<Value>();
        while (buffer.hasRemaining()) {
            fields.add(Value.deserialize(buffer));
        }

        return new InstanceEntry(objectId, classId, fields);
    }
}
