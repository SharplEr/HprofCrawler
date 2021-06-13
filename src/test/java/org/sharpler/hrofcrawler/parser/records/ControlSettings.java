package org.sharpler.hrofcrawler.parser.records;

import java.io.DataOutputStream;
import java.io.IOException;

public final class ControlSettings implements RecordBase {
    private final int bitMaskFlags;
    private final short stackTraceDepth;

    public ControlSettings(int bitMaskFlags, short stackTraceDepth) {
        this.bitMaskFlags = bitMaskFlags;
        this.stackTraceDepth = stackTraceDepth;
    }

    @Override
    public void storeInto(DataOutputStream output) throws IOException {
        output.writeByte(0xe);
        output.writeInt(0);
        output.writeInt(4 + 2);
        output.writeInt(bitMaskFlags);
        output.writeShort(stackTraceDepth);
    }

    @Override
    public String toString() {
        return "ControlSettings{" +
                "bitMaskFlags=" + bitMaskFlags +
                ", stackTraceDepth=" + stackTraceDepth +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ControlSettings that = (ControlSettings) o;

        if (bitMaskFlags != that.bitMaskFlags) return false;
        return stackTraceDepth == that.stackTraceDepth;
    }

    @Override
    public int hashCode() {
        int result = bitMaskFlags;
        result = 31 * result + (int) stackTraceDepth;
        return result;
    }
}
