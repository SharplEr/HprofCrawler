package org.sharpler.hrofcrawler.parser;

public class InstanceField {
    public final long fieldNameStringId;
    public final Type type;

    public InstanceField(long fieldNameStringId, Type type) {
        this.fieldNameStringId = fieldNameStringId;
        this.type = type;
    }
}
