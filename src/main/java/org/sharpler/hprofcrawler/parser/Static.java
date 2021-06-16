package org.sharpler.hprofcrawler.parser;

public class Static {
    public final long staticFieldNameStringId;
    public final Value value;

    public Static(long staticFieldNameStringId, Value value) {
        this.staticFieldNameStringId = staticFieldNameStringId;
        this.value = value;
    }
}
