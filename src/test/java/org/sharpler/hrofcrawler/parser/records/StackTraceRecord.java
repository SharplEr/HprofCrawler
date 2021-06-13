package org.sharpler.hrofcrawler.parser.records;

import org.sharpler.hrofcrawler.parser.HprofParserTest;

import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Arrays;

public final class StackTraceRecord implements RecordBase {
    private final int stackTraceSerialNum;
    private final int threadSerialNum;
    private final int numFrames;
    private final long[] stackFrameIds;
    private final int idSize;

    public StackTraceRecord(int stackTraceSerialNum, int threadSerialNum, int numFrames, long[] stackFrameIds, int idSize) {
        this.stackTraceSerialNum = stackTraceSerialNum;
        this.threadSerialNum = threadSerialNum;
        this.numFrames = numFrames;
        this.stackFrameIds = stackFrameIds;
        this.idSize = idSize;
    }

    @Override
    public void storeInto(DataOutputStream output) throws IOException {
        output.writeByte(0x5);
        output.writeInt(0);
        output.writeInt(3 * 4 + stackFrameIds.length * idSize);
        output.writeInt(stackTraceSerialNum);
        output.writeInt(threadSerialNum);
        output.writeInt(numFrames);
        for (var id : stackFrameIds) {
            HprofParserTest.storeIntoStream(idSize, id, output);
        }
    }

    @Override
    public String toString() {
        return "StackTraceRecord{" +
                "stackTraceSerialNum=" + stackTraceSerialNum +
                ", threadSerialNum=" + threadSerialNum +
                ", numFrames=" + numFrames +
                ", stackFrameIds=" + Arrays.toString(stackFrameIds) +
                ", idSize=" + idSize +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        StackTraceRecord that = (StackTraceRecord) o;

        if (stackTraceSerialNum != that.stackTraceSerialNum) return false;
        if (threadSerialNum != that.threadSerialNum) return false;
        if (numFrames != that.numFrames) return false;
        if (idSize != that.idSize) return false;
        return Arrays.equals(stackFrameIds, that.stackFrameIds);
    }

    @Override
    public int hashCode() {
        int result = stackTraceSerialNum;
        result = 31 * result + threadSerialNum;
        result = 31 * result + numFrames;
        result = 31 * result + Arrays.hashCode(stackFrameIds);
        result = 31 * result + idSize;
        return result;
    }
}
