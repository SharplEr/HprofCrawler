package org.sharpler.hprofcrawler.parser;

public class CPUSample {
    public final int numSamples;
    public final int stackTraceSerialNum;

    public CPUSample(int numSamples, int stackTraceSerialNum) {
        this.numSamples = numSamples;
        this.stackTraceSerialNum = stackTraceSerialNum;
    }
}

