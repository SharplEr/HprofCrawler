package org.sharpler.hprofcrawler.parser;

public class Constant {
    public final short constantPoolIndex;
    public final Value value;

    public Constant(short constantPoolIndex, Value value) {
        this.constantPoolIndex = constantPoolIndex;
        this.value = value;
    }
}
