package org.sharpler.hrofcrawler.parser;

public class InstanceField {
    public long fieldNameStringId;
    public Type type;

    public InstanceField(long fieldNameStringId, Type type) {
        this.fieldNameStringId = fieldNameStringId;
        this.type = type;
    }
}
