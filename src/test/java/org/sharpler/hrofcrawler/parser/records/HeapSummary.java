package org.sharpler.hrofcrawler.parser.records;

import java.io.DataOutputStream;
import java.io.IOException;

public final class HeapSummary implements RecordBase {
    private final int totalLiveBytes;
    private final int totalLiveInstances;
    private final long totalBytesAllocated;
    private final long totalInstancesAllocated;

    public HeapSummary(int totalLiveBytes, int totalLiveInstances, long totalBytesAllocated, long totalInstancesAllocated) {
        this.totalLiveBytes = totalLiveBytes;
        this.totalLiveInstances = totalLiveInstances;
        this.totalBytesAllocated = totalBytesAllocated;
        this.totalInstancesAllocated = totalInstancesAllocated;
    }

    @Override
    public void storeInto(DataOutputStream output) throws IOException {
        output.writeByte(0x7);
        output.writeInt(0);
        output.writeInt(2 * 4 + 2 * 8);

        output.writeInt(totalLiveBytes);
        output.writeInt(totalLiveInstances);
        output.writeLong(totalBytesAllocated);
        output.writeLong(totalInstancesAllocated);
    }

    @Override
    public String toString() {
        return "HeapSummary{" +
                "totalLiveBytes=" + totalLiveBytes +
                ", totalLiveInstances=" + totalLiveInstances +
                ", totalBytesAllocated=" + totalBytesAllocated +
                ", totalInstancesAllocated=" + totalInstancesAllocated +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        HeapSummary that = (HeapSummary) o;

        if (totalLiveBytes != that.totalLiveBytes) return false;
        if (totalLiveInstances != that.totalLiveInstances) return false;
        if (totalBytesAllocated != that.totalBytesAllocated) return false;
        return totalInstancesAllocated == that.totalInstancesAllocated;
    }

    @Override
    public int hashCode() {
        int result = totalLiveBytes;
        result = 31 * result + totalLiveInstances;
        result = 31 * result + (int) (totalBytesAllocated ^ (totalBytesAllocated >>> 32));
        result = 31 * result + (int) (totalInstancesAllocated ^ (totalInstancesAllocated >>> 32));
        return result;
    }
}
