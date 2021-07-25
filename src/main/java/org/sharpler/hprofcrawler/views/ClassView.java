package org.sharpler.hprofcrawler.views;

import org.sharpler.hprofcrawler.parser.InstanceField;
import org.sharpler.hprofcrawler.parser.Type;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

public final class ClassView {
    private final long name;
    private final long id;
    private final long superClassId;
    private final int instanceSize;
    private final List<InstanceField> fields;

    private int count = 0;

    private ClassView(long name, long id, long superClassId, int instanceSize, int count, List<InstanceField> fields) {
        this.name = name;
        this.id = id;
        this.superClassId = superClassId;
        this.instanceSize = instanceSize;
        this.fields = fields;
        this.count = count;
    }

    public static ClassView create(long name, long id, long superClassId, int instanceSize, List<InstanceField> fields) {
        return new ClassView(name, id, superClassId, instanceSize, 0, fields);
    }

    public long getName() {
        return name;
    }

    public long getId() {
        return id;
    }

    public long getSuperClassId() {
        return superClassId;
    }

    public int getInstanceSize() {
        return instanceSize;
    }

    public List<InstanceField> getFields() {
        return fields;
    }

    public int getCount() {
        return count;
    }

    public boolean isNotEmpty() {
        return count != 0;
    }

    public void addCount() {
        count++;
    }

    public byte[] serialize() {
        var buffer = ByteBuffer.allocate(3 * Long.BYTES + 2 * Integer.BYTES + fields.size() * (Long.BYTES + 1));
        buffer.putLong(name);
        buffer.putLong(id);
        buffer.putLong(superClassId);
        buffer.putInt(instanceSize);
        buffer.putInt(count);

        for (var field : fields) {
            buffer.putLong(field.getFieldNameStringId());
            buffer.put((byte) field.getType().ordinal());
        }

        return buffer.array();
    }

    public static ClassView deserialize(byte[] data) {
        var buffer = ByteBuffer.wrap(data);
        return new ClassView(
                buffer.getLong(),
                buffer.getLong(),
                buffer.getLong(),
                buffer.getInt(),
                buffer.getInt(),
                deserializeFields(buffer)
        );
    }

    private static List<InstanceField> deserializeFields(ByteBuffer buffer) {
        var result = new ArrayList<InstanceField>(buffer.remaining() / (Long.BYTES + 1));
        while (buffer.hasRemaining()) {
            result.add(new InstanceField(buffer.getLong(), Type.VALUES.get(buffer.get())));
        }
        return result;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ClassView)) return false;

        ClassView classView = (ClassView) o;

        if (id != classView.id) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return (int) (id ^ (id >>> 32));
    }

    @Override
    public String toString() {
        return "ClassView{" +
                "name='" + name + '\'' +
                ", id=" + id +
                ", superClassId=" + superClassId +
                ", instanceSize=" + instanceSize +
                ", fields=" + fields +
                ", count=" + count +
                '}';
    }
}
