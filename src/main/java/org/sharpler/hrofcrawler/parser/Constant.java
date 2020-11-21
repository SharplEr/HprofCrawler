package org.sharpler.hrofcrawler.parser;

public class Constant {
    public short constantPoolIndex;
    public Value value;

    public Constant(short constantPoolIndex, Value value) {
        this.constantPoolIndex = constantPoolIndex;
        this.value = value;
    }
}
