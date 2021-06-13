package org.sharpler.hrofcrawler.parser.records;

import java.io.DataOutputStream;
import java.io.IOException;

public final class EndThreadRecord implements RecordBase {
    private final int threadSerialNum;

    public EndThreadRecord(int threadSerialNum) {
        this.threadSerialNum = threadSerialNum;
    }

    @Override
    public void storeInto(DataOutputStream output) throws IOException {
        output.writeByte(0xb);
        output.writeInt(0);
        output.writeInt(4);
        output.writeInt(threadSerialNum);
    }

    @Override
    public String toString() {
        return "EndThreadRecord{" +
                "threadSerialNum=" + threadSerialNum +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        EndThreadRecord that = (EndThreadRecord) o;

        return threadSerialNum == that.threadSerialNum;
    }

    @Override
    public int hashCode() {
        return threadSerialNum;
    }
}
