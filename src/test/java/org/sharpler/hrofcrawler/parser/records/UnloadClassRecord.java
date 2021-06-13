package org.sharpler.hrofcrawler.parser.records;

import java.io.DataOutputStream;
import java.io.IOException;

public final class UnloadClassRecord implements RecordBase {
    private final int classSerialNum;

    public UnloadClassRecord(int classSerialNum) {
        this.classSerialNum = classSerialNum;
    }

    @Override
    public void storeInto(DataOutputStream output) throws IOException {
        output.writeByte(0x3);
        output.writeInt(0);
        output.writeInt(4);
        output.writeInt(classSerialNum);
    }

    @Override
    public String toString() {
        return "UnloadClassRecord{" +
                "classSerialNum=" + classSerialNum +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        UnloadClassRecord that = (UnloadClassRecord) o;

        return classSerialNum == that.classSerialNum;
    }

    @Override
    public int hashCode() {
        return classSerialNum;
    }
}
