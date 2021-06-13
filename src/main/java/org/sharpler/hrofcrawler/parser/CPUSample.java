package org.sharpler.hrofcrawler.parser;

public class CPUSample {
    public final int numSamples;
    public final int stackTraceSerialNum;

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

    @Override
    public String toString() {
        return "CPUSample{" +
                "numSamples=" + numSamples +
                ", stackTraceSerialNum=" + stackTraceSerialNum +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CPUSample cpuSample = (CPUSample) o;

        if (numSamples != cpuSample.numSamples) return false;
        return stackTraceSerialNum == cpuSample.stackTraceSerialNum;
    }

    @Override
    public int hashCode() {
        int result = numSamples;
        result = 31 * result + stackTraceSerialNum;
        return result;
    }
}

