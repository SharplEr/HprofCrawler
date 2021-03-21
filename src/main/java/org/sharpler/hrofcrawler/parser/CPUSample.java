package org.sharpler.hrofcrawler.parser;

public final class CPUSample {
    private final int numSamples;
    private final int stackTraceSerialNum;

    public CPUSample(int numSamples, int stackTraceSerialNum) {
        this.numSamples = numSamples;
        this.stackTraceSerialNum = stackTraceSerialNum;
    }

    public int getNumSamples() {
        return numSamples;
    }

    public int getStackTraceSerialNum() {
        return stackTraceSerialNum;
    }
}

