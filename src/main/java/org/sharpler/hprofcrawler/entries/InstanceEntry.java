package org.sharpler.hprofcrawler.entries;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.sharpler.hprofcrawler.parser.Value;

public final class InstanceEntry {
    @JsonProperty("objectId")
    private final long objectId;
    @JsonProperty("classId")
    private final long classId;
    @JsonProperty("fields")
    private final List<Value> fields;

    @JsonCreator
    public InstanceEntry(
            @JsonProperty("objectId")
                    long objectId,
            @JsonProperty("classId")
                    long classId,
            @JsonProperty("fields")
                    List<Value> fields)
    {
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
}
