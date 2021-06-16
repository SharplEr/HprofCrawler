package org.sharpler.hprofcrawler.parser;

public class Instance {
    public final long objId;
    public final int stackTraceSerialNum;
    public final long classObjId;
    public final byte[] packedValues;    // will use ByteArrayInputStream to read this

    public Instance(long objId, int stackTraceSerialNum, long classObjId,
                    byte[] packedValues) {
        this.objId = objId;
        this.stackTraceSerialNum = stackTraceSerialNum;
        this.classObjId = classObjId;
        this.packedValues = packedValues;
    }

}
