package org.sharpler.hprofcrawler.views;

import org.sharpler.hprofcrawler.Utils;
import org.sharpler.hprofcrawler.parser.InstanceField;
import org.sharpler.hprofcrawler.parser.Type;
import org.sharpler.hprofcrawler.parser.Value;

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
            buffer.put(Utils.toByteExact(field.getType().ordinal()));
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

    public List<Value> deserializePackedFieldValues(ByteBuffer buffer) {
        if (fields.isEmpty()) {
            return List.of();
        }

        var result = new ArrayList<Value>(fields.size());
        for (var field : fields) {
            var value = switch (field.getType()) {
                case OBJ -> Value.ofLong(buffer.getLong(), true);
                case BOOL -> Value.ofBool(buffer.get() != 0);
                case CHAR -> Value.ofChar(buffer.getChar());
                case FLOAT -> Value.ofFloat(buffer.getFloat());
                case DOUBLE -> Value.ofDouble(buffer.getDouble());
                case BYTE -> Value.ofByte(buffer.get());
                case SHORT -> Value.ofShort(buffer.getShort());
                case INT -> Value.ofInt(buffer.getInt());
                case LONG -> Value.ofLong(buffer.getLong(), false);
            };
            result.add(value);
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
