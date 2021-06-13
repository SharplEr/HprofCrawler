package org.sharpler.hrofcrawler.parser.records;

import org.sharpler.hrofcrawler.parser.HprofParserTest;

import java.io.DataOutputStream;
import java.io.IOException;

public final class StartThreadRecord implements RecordBase {
    private final int threadSerialNum;
    private final long threadObjectId;
    private final int stackTraceSerialNum;
    private final long threadNameStringId;
    private final long threadGroupNameId;
    private final long threadParentGroupNameId;
    private final int idSize;

    public StartThreadRecord(
            int threadSerialNum,
            long threadObjectId,
            int stackTraceSerialNum,
            long threadNameStringId,
            long threadGroupNameId,
            long threadParentGroupNameId,
            int idSize
    ) {
        this.threadSerialNum = threadSerialNum;
        this.threadObjectId = threadObjectId;
        this.stackTraceSerialNum = stackTraceSerialNum;
        this.threadNameStringId = threadNameStringId;
        this.threadGroupNameId = threadGroupNameId;
        this.threadParentGroupNameId = threadParentGroupNameId;
        this.idSize = idSize;
    }

    @Override
    public void storeInto(DataOutputStream output) throws IOException {
        output.writeByte(0xa);
        output.writeInt(0);
        output.writeInt(4 * idSize + 2 * 4);

        output.writeInt(threadSerialNum);
        HprofParserTest.storeIntoStream(idSize, threadObjectId, output);
        output.writeInt(stackTraceSerialNum);
        HprofParserTest.storeIntoStream(idSize, threadNameStringId, output);
        HprofParserTest.storeIntoStream(idSize, threadGroupNameId, output);
        HprofParserTest.storeIntoStream(idSize, threadParentGroupNameId, output);

    }

    @Override
    public String toString() {
        return "StartThreadRecord{" +
                "threadSerialNum=" + threadSerialNum +
                ", threadObjectId=" + threadObjectId +
                ", stackTraceSerialNum=" + stackTraceSerialNum +
                ", threadNameStringId=" + threadNameStringId +
                ", threadGroupNameId=" + threadGroupNameId +
                ", threadParentGroupNameId=" + threadParentGroupNameId +
                ", idSize=" + idSize +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        StartThreadRecord that = (StartThreadRecord) o;

        if (threadSerialNum != that.threadSerialNum) return false;
        if (threadObjectId != that.threadObjectId) return false;
        if (stackTraceSerialNum != that.stackTraceSerialNum) return false;
        if (threadNameStringId != that.threadNameStringId) return false;
        if (threadGroupNameId != that.threadGroupNameId) return false;
        if (threadParentGroupNameId != that.threadParentGroupNameId) return false;
        return idSize == that.idSize;
    }

    @Override
    public int hashCode() {
        int result = threadSerialNum;
        result = 31 * result + (int) (threadObjectId ^ (threadObjectId >>> 32));
        result = 31 * result + stackTraceSerialNum;
        result = 31 * result + (int) (threadNameStringId ^ (threadNameStringId >>> 32));
        result = 31 * result + (int) (threadGroupNameId ^ (threadGroupNameId >>> 32));
        result = 31 * result + (int) (threadParentGroupNameId ^ (threadParentGroupNameId >>> 32));
        result = 31 * result + idSize;
        return result;
    }
}
