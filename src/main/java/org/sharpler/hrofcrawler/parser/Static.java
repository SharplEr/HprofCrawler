package org.sharpler.hrofcrawler.parser;

public final class Static {
    private final long staticFieldNameStringId;
    private final Value value;

    public Static(long staticFieldNameStringId, Value value) {
        this.staticFieldNameStringId = staticFieldNameStringId;
        this.value = value;
    }

    public long getStaticFieldNameStringId() {
        return staticFieldNameStringId;
    }

    public Value getValue() {
        return value;
    }
}
