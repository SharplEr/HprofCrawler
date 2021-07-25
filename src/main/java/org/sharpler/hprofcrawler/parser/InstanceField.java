package org.sharpler.hprofcrawler.parser;

public class InstanceField {
    private final long fieldNameStringId;
    private final Type type;

    public InstanceField(long fieldNameStringId, Type type) {
        this.fieldNameStringId = fieldNameStringId;
        this.type = type;
    }

    public long getFieldNameStringId() {
        return fieldNameStringId;
    }

    public Type getType() {
        return type;
    }
}
