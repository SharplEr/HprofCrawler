package org.sharpler.hrofcrawler.parser;

public final class AllocSite {
    private final byte arrayIndicator;
    private final int classSerialNum;
    private final int stackTraceSerialNum;
    private final int numLiveBytes;
    private final int numLiveInstances;
    private final int numBytesAllocated;
    private final int numInstancesAllocated;

    public AllocSite(
            byte arrayIndicator,
            int classSerialNum,
            int stackTraceSerialNum,
            int numLiveBytes,
            int numLiveInstances,
            int numBytesAllocated,
            int numInstancesAllocated
    ) {
        this.arrayIndicator = arrayIndicator;
        this.classSerialNum = classSerialNum;
        this.stackTraceSerialNum = stackTraceSerialNum;
        this.numLiveBytes = numLiveBytes;
        this.numLiveInstances = numLiveInstances;
        this.numBytesAllocated = numBytesAllocated;
        this.numInstancesAllocated = numInstancesAllocated;
    }

    public byte getArrayIndicator() {
        return arrayIndicator;
    }

    public int getClassSerialNum() {
        return classSerialNum;
    }

    public int getStackTraceSerialNum() {
        return stackTraceSerialNum;
    }

    public int getNumLiveBytes() {
        return numLiveBytes;
    }

    public int getNumLiveInstances() {
        return numLiveInstances;
    }

    public int getNumBytesAllocated() {
        return numBytesAllocated;
    }

    public int getNumInstancesAllocated() {
        return numInstancesAllocated;
    }
}
