package org.sharpler.hrofcrawler.parser;

public final class Constant {
    private final short constantPoolIndex;
    private final Value value;

    public Constant(short constantPoolIndex, Value value) {
        this.constantPoolIndex = constantPoolIndex;
        this.value = value;
    }

    public short getConstantPoolIndex() {
        return constantPoolIndex;
    }

    public Value getValue() {
        return value;
    }
}
