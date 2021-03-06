package org.sharpler.hprofcrawler.parser;

public class AllocSite {
    public final byte arrayIndicator;
    public final int classSerialNum;
    public final int stackTraceSerialNum;
    public final int numLiveBytes;
    public final int numLiveInstances;
    public final int numBytesAllocated;
    public final int numInstancesAllocated;

    public AllocSite(byte arrayIndicator, int classSerialNum,
                     int stackTraceSerialNum, int numLiveBytes, int numLiveInstances,
                     int numBytesAllocated, int numInstancesAllocated) {

        this.arrayIndicator = arrayIndicator;
        this.classSerialNum = classSerialNum;
        this.stackTraceSerialNum = stackTraceSerialNum;
        this.numLiveBytes = numLiveBytes;
        this.numLiveInstances = numLiveInstances;
        this.numBytesAllocated = numBytesAllocated;
        this.numInstancesAllocated = numInstancesAllocated;

    }
}
