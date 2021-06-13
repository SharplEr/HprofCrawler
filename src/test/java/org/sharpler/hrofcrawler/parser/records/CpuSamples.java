package org.sharpler.hrofcrawler.parser.records;

import org.sharpler.hrofcrawler.parser.CPUSample;

import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Arrays;

public final class CpuSamples implements RecordBase {
    private final int totalNumOfSamples;
    private final CPUSample[] samples;

    public CpuSamples(int totalNumOfSamples, CPUSample[] samples) {
        this.totalNumOfSamples = totalNumOfSamples;
        this.samples = samples;
    }

    @Override
    public void storeInto(DataOutputStream output) throws IOException {
        output.writeByte(0xd);
        output.writeInt(0);
        output.writeInt(4 + 4 + (4 + 4) * samples.length);

        output.writeInt(totalNumOfSamples);
        output.writeInt(samples.length);
        for (var sample : samples) {
            output.writeInt(sample.getNumSamples());
            output.writeInt(sample.getStackTraceSerialNum());
        }
    }

    @Override
    public String toString() {
        return "CpuSamples{" +
                "totalNumOfSamples=" + totalNumOfSamples +
                ", samples=" + Arrays.toString(samples) +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CpuSamples that = (CpuSamples) o;

        if (totalNumOfSamples != that.totalNumOfSamples) return false;
        return Arrays.equals(samples, that.samples);
    }

    @Override
    public int hashCode() {
        int result = totalNumOfSamples;
        result = 31 * result + Arrays.hashCode(samples);
        return result;
    }
}
