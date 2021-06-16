package org.sharpler.hprofcrawler.views;

import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import org.sharpler.hprofcrawler.entries.ClassEntry;
import org.sharpler.hprofcrawler.entries.FieldEntry;
import org.sharpler.hprofcrawler.parser.Type;

import java.util.List;

public final class ClassView {
    private final String name;
    private final long id;
    private final long superClassId;
    private final int instanceSize;
    private final List<FieldEntry> fields;

    private final Object2IntOpenHashMap<String> fieldsIndex;

    private final boolean isFlat;

    private int count = 0;

    public ClassView(String name, long id, long superClassId, int instanceSize, List<FieldEntry> fields) {
        this.name = name;
        this.id = id;
        this.superClassId = superClassId;
        this.instanceSize = instanceSize;
        this.fields = fields;
        this.isFlat = fields.stream().noneMatch(x -> x.getType() == Type.OBJ);
        fieldsIndex = new Object2IntOpenHashMap<>(fields.size());

        fieldsIndex.defaultReturnValue(-1);
        for (int i = 0; i < fields.size(); i++) {
            fieldsIndex.put(fields.get(i).getName(), i);
        }
    }

    public static ClassView of(ClassEntry entry) {
        return new ClassView(
                entry.getName(),
                entry.getId(),
                entry.getSuperClassId(),
                entry.getInstanceSize(),
                entry.getFields()
        );
    }

    public String getName() {
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

    public List<FieldEntry> getFields() {
        return fields;
    }

    public boolean isFlat() {
        return isFlat;
    }

    public int getCount() {
        return count;
    }

    public boolean isNotEmpty() {
        return count != 0;
    }

    public int fieldIndex(String name) {
        return fieldsIndex.getInt(name);
    }

    public void addCount() {
        count++;
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
                ", fieldsIndex=" + fieldsIndex +
                ", isFlat=" + isFlat +
                ", count=" + count +
                '}';
    }
}
