package org.sharpler.hrofcrawler.parser.records;

import org.jooq.lambda.fi.lang.CheckedRunnable;
import org.jooq.lambda.fi.util.function.CheckedConsumer;
import org.jooq.lambda.fi.util.function.CheckedIntConsumer;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.util.List;

public final class HeapDump {
    private final String format;
    private final int identifierSize;
    private final long startTime;
    private final List<RecordBase> records;

    public HeapDump(String format, int identifierSize, long startTime, List<RecordBase> records) {
        if (format.indexOf(0) != -1) {
            throw new IllegalArgumentException("format has 0s: " + format);
        }
        if (identifierSize != 4 && identifierSize != 8) {
            throw new IllegalArgumentException("Invalid identifierSize: " + identifierSize);
        }
        this.format = format;
        this.identifierSize = identifierSize;
        this.startTime = startTime;
        this.records = records;
    }

    public DataInputStream toStream() {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        var output = new DataOutputStream(byteArrayOutputStream);
        format.chars().forEach(CheckedIntConsumer.unchecked(output::writeByte));
        CheckedRunnable.unchecked(
                () -> {
                    output.writeByte(0);
                    output.writeInt(identifierSize);
                    output.writeLong(startTime);
                    output.close();
                }
        ).run();

        records.forEach(CheckedConsumer.unchecked(r -> r.storeInto(output)));

        byte[] bytes = byteArrayOutputStream.toByteArray();

        return new DataInputStream(new ByteArrayInputStream(bytes));
    }

    public String getFormat() {
        return format;
    }

    public int getIdentifierSize() {
        return identifierSize;
    }

    public long getStartTime() {
        return startTime;
    }

    public List<RecordBase> getRecords() {
        return records;
    }

    @Override
    public String toString() {
        return "HeapDump{" +
                "format='" + format + '\'' +
                ", identifierSize=" + identifierSize +
                ", startTime=" + startTime +
                ", records=" + records +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        HeapDump heapDump = (HeapDump) o;

        if (identifierSize != heapDump.identifierSize) return false;
        if (startTime != heapDump.startTime) return false;
        if (!format.equals(heapDump.format)) return false;
        return records.equals(heapDump.records);
    }

    @Override
    public int hashCode() {
        int result = format.hashCode();
        result = 31 * result + identifierSize;
        result = 31 * result + (int) (startTime ^ (startTime >>> 32));
        result = 31 * result + records.hashCode();
        return result;
    }
}
