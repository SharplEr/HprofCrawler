package org.sharpler.hrofcrawler.entries;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.sharpler.hrofcrawler.parser.Type;

public final class FieldEntry {
    @JsonProperty("name")
    private final String name;
    @JsonProperty("type")
    private final Type type;

    @JsonCreator
    public FieldEntry(@JsonProperty("name") String name, @JsonProperty("type") Type type) {
        this.name = name;
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public Type getType() {
        return type;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof FieldEntry)) return false;

        FieldEntry that = (FieldEntry) o;

        if (!name.equals(that.name)) return false;
        if (type != that.type) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = name.hashCode();
        result = 31 * result + type.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "FieldView{" +
                "name='" + name + '\'' +
                ", type=" + type +
                '}';
    }
}
