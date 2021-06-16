package org.sharpler.hprofcrawler.entries;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public final class ClassEntry {
    @JsonProperty("name")
    private final String name;
    @JsonProperty("id")
    private final long id;
    @JsonProperty("superClassId")
    private final long superClassId;
    @JsonProperty("instanceSize")
    private final int instanceSize;
    @JsonProperty("fields")
    private final List<FieldEntry> fields;
    @JsonProperty("count")
    private final int count;

    @JsonCreator
    public ClassEntry(
            @JsonProperty("name")
                    String name,
            @JsonProperty("id")
                    long id,
            @JsonProperty("superClassId")
                    long superClassId,
            @JsonProperty("instanceSize")
                    int instanceSize,
            @JsonProperty("fields")
                    List<FieldEntry> fields,
            @JsonProperty("count")
                    int count)
    {
        this.name = name;
        this.id = id;
        this.superClassId = superClassId;
        this.instanceSize = instanceSize;
        this.fields = fields;
        this.count = count;
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

    public int getCount() {
        return count;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ClassEntry)) return false;

        ClassEntry entry = (ClassEntry) o;

        if (id != entry.id) return false;
        if (superClassId != entry.superClassId) return false;
        if (instanceSize != entry.instanceSize) return false;
        if (count != entry.count) return false;
        if (!name.equals(entry.name)) return false;
        if (!fields.equals(entry.fields)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = name.hashCode();
        result = 31 * result + (int) (id ^ (id >>> 32));
        result = 31 * result + (int) (superClassId ^ (superClassId >>> 32));
        result = 31 * result + instanceSize;
        result = 31 * result + fields.hashCode();
        result = 31 * result + count;
        return result;
    }

    @Override
    public String toString() {
        return "ClassEntry{" +
                "name='" + name + '\'' +
                ", id=" + id +
                ", superClassId=" + superClassId +
                ", instanceSize=" + instanceSize +
                ", fields=" + fields +
                ", count=" + count +
                '}';
    }
}
