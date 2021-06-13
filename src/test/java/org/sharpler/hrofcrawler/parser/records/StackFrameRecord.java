package org.sharpler.hrofcrawler.parser.records;

import org.sharpler.hrofcrawler.parser.HprofParserTest;

import java.io.DataOutputStream;
import java.io.IOException;

public final class StackFrameRecord implements RecordBase {
    private final long stackFrameId;
    private final long methodNameStringId;
    private final long methodSigStringId;
    private final long sourceFileNameStringId;
    private final int classSerialNum;
    private final int location;
    private final int idSize;

    public StackFrameRecord(long stackFrameId, long methodNameStringId, long methodSigStringId, long sourceFileNameStringId, int classSerialNum, int location, int idSize) {
        this.stackFrameId = stackFrameId;
        this.methodNameStringId = methodNameStringId;
        this.methodSigStringId = methodSigStringId;
        this.sourceFileNameStringId = sourceFileNameStringId;
        this.classSerialNum = classSerialNum;
        this.location = location;
        this.idSize = idSize;
    }

    @Override
    public void storeInto(DataOutputStream output) throws IOException {
        output.writeByte(0x4);
        output.writeInt(0);
        output.writeInt(idSize * 4 + 2 * 4);
        HprofParserTest.storeIntoStream(idSize, stackFrameId, output);
        HprofParserTest.storeIntoStream(idSize, methodNameStringId, output);
        HprofParserTest.storeIntoStream(idSize, methodSigStringId, output);
        HprofParserTest.storeIntoStream(idSize, sourceFileNameStringId, output);
        output.writeInt(classSerialNum);
        output.writeInt(location);
    }

    @Override
    public String toString() {
        return "StackFrameRecord{" +
                "stackFrameId=" + stackFrameId +
                ", methodNameStringId=" + methodNameStringId +
                ", methodSigStringId=" + methodSigStringId +
                ", sourceFileNameStringId=" + sourceFileNameStringId +
                ", classSerialNum=" + classSerialNum +
                ", location=" + location +
                ", idSize=" + idSize +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        StackFrameRecord that = (StackFrameRecord) o;

        if (stackFrameId != that.stackFrameId) return false;
        if (methodNameStringId != that.methodNameStringId) return false;
        if (methodSigStringId != that.methodSigStringId) return false;
        if (sourceFileNameStringId != that.sourceFileNameStringId) return false;
        if (classSerialNum != that.classSerialNum) return false;
        if (location != that.location) return false;
        return idSize == that.idSize;
    }

    @Override
    public int hashCode() {
        int result = (int) (stackFrameId ^ (stackFrameId >>> 32));
        result = 31 * result + (int) (methodNameStringId ^ (methodNameStringId >>> 32));
        result = 31 * result + (int) (methodSigStringId ^ (methodSigStringId >>> 32));
        result = 31 * result + (int) (sourceFileNameStringId ^ (sourceFileNameStringId >>> 32));
        result = 31 * result + classSerialNum;
        result = 31 * result + location;
        result = 31 * result + idSize;
        return result;
    }
}
