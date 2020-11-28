package org.sharpler.hrofcrawler.parser;

public class ClassInfo {
    private final long classObjId;
    private final long superClassObjId;
    private final int instanceSize;
    private final InstanceField[] instanceFields;

    public ClassInfo(long classObjId, long superClassObjId, int instanceSize, InstanceField[] instanceFields) {
        this.classObjId = classObjId;
        this.superClassObjId = superClassObjId;
        this.instanceSize = instanceSize;
        this.instanceFields = instanceFields;
    }

    public long getClassObjId() {
        return classObjId;
    }

    public long getSuperClassObjId() {
        return superClassObjId;
    }

    public int getInstanceSize() {
        return instanceSize;
    }

    public InstanceField[] getInstanceFields() {
        return instanceFields;
    }
}
