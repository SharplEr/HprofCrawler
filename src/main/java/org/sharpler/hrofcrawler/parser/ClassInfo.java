package org.sharpler.hrofcrawler.parser;

public class ClassInfo {
    public long classObjId;
    public long superClassObjId;
    public int instanceSize;
    public InstanceField[] instanceFields;

    public ClassInfo() {}

    public ClassInfo(long classObjId, long superClassObjId, int instanceSize, InstanceField[] instanceFields) {
        this.classObjId = classObjId;
        this.superClassObjId = superClassObjId;
        this.instanceSize = instanceSize;
        this.instanceFields = instanceFields;
    }
}
