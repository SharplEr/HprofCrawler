package org.sharpler.hrofcrawler.parser;

import java.util.Arrays;
import java.util.List;

public enum Type {
    OBJ("Object", 4),
    BOOL("boolean", 1),
    CHAR("char", 2),
    FLOAT("float", 4),
    DOUBLE("double", 8),
    BYTE("byte", 1),
    SHORT("short", 2),
    INT("int", 4),
    LONG("long", 8);

    public static final List<Type> VALUES = Arrays.asList(values());

    private final String name;
    private final int sizeInBytes;

    private Type(String name, int sizeInBytes) {
        this.name = name;
        this.sizeInBytes = sizeInBytes;
    }

    public int sizeInBytes() {
        return sizeInBytes;
    }

    public static Type hprofTypeToEnum(byte type) {
        switch (type) {
            case 2:
                return OBJ;
            case 4:
                return BOOL;
            case 5:
                return CHAR;
            case 6:
                return FLOAT;
            case 7:
                return DOUBLE;
            case 8:
                return BYTE;
            case 9:
                return SHORT;
            case 10:
                return INT;
            case 11:
                return LONG;
            default:
                throw new IllegalArgumentException("Unexpected type in heap dump: " + type);
        }
    }

    @Override
    public String toString() {
        return name;
    }
}
