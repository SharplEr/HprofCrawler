package org.sharpler.hrofcrawler.parser.records;

import org.sharpler.hrofcrawler.parser.HprofParserTest;

import java.io.DataOutputStream;
import java.io.IOException;

public final class LoadClassRecord implements RecordBase {
    private final int classSerialNum;
    private final long classObjId;
    private final int stackTraceSerialNum;
    private final long classNameStringId;
    private final int idSize;

    public LoadClassRecord(int classSerialNum, long classObjId, int stackTraceSerialNum, long classNameStringId, int idSize) {
        this.classSerialNum = classSerialNum;
        this.classObjId = classObjId;
        this.stackTraceSerialNum = stackTraceSerialNum;
        this.classNameStringId = classNameStringId;
        this.idSize = idSize;
    }


    @Override
    public void storeInto(DataOutputStream output) throws IOException {
        output.writeByte(0x2);
        output.writeInt(0);
        output.writeInt(4 + idSize + 4 + idSize);
        output.writeInt(classSerialNum);
        HprofParserTest.storeIntoStream(idSize, classObjId, output);
        output.writeInt(stackTraceSerialNum);
        HprofParserTest.storeIntoStream(idSize, classNameStringId, output);
    }

    @Override
    public String toString() {
        return "LoadClassRecord{" +
                "classSerialNum=" + classSerialNum +
                ", classObjId=" + classObjId +
                ", stackTraceSerialNum=" + stackTraceSerialNum +
                ", classNameStringId=" + classNameStringId +
                ", idSize=" + idSize +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        LoadClassRecord that = (LoadClassRecord) o;

        if (classSerialNum != that.classSerialNum) return false;
        if (classObjId != that.classObjId) return false;
        if (stackTraceSerialNum != that.stackTraceSerialNum) return false;
        if (classNameStringId != that.classNameStringId) return false;
        return idSize == that.idSize;
    }

    @Override
    public int hashCode() {
        int result = classSerialNum;
        result = 31 * result + (int) (classObjId ^ (classObjId >>> 32));
        result = 31 * result + stackTraceSerialNum;
        result = 31 * result + (int) (classNameStringId ^ (classNameStringId >>> 32));
        result = 31 * result + idSize;
        return result;
    }
}
