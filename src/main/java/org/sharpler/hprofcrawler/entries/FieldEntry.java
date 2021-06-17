package org.sharpler.hprofcrawler.entries;

import org.sharpler.hprofcrawler.parser.Type;

public final class FieldEntry {
    private final String name;
    private final Type type;

    public FieldEntry(String name, Type type) {
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
