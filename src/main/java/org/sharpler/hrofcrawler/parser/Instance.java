package org.sharpler.hrofcrawler.parser;

public final class Instance {
    private final long objId;
    private final int stackTraceSerialNum;
    private final long classObjId;
    private final byte[] packedValues;    // will use ByteArrayInputStream to read this

    public Instance(
            long objId,
            int stackTraceSerialNum,
            long classObjId,
            byte[] packedValues
    ) {
        this.objId = objId;
        this.stackTraceSerialNum = stackTraceSerialNum;
        this.classObjId = classObjId;
        this.packedValues = packedValues;
    }

    public long getObjId() {
        return objId;
    }

    public int getStackTraceSerialNum() {
        return stackTraceSerialNum;
    }

    public long getClassObjId() {
        return classObjId;
    }

    public byte[] getPackedValues() {
        return packedValues;
    }
}
