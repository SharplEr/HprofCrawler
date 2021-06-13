package org.sharpler.hrofcrawler.parser;

import org.jooq.lambda.fi.util.function.CheckedSupplier;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.sharpler.hrofcrawler.concurrent.Lazy;
import org.sharpler.hrofcrawler.parser.records.ControlSettings;
import org.sharpler.hrofcrawler.parser.records.CpuSamples;
import org.sharpler.hrofcrawler.parser.records.EndThreadRecord;
import org.sharpler.hrofcrawler.parser.records.HeapDump;
import org.sharpler.hrofcrawler.parser.records.HeapSummary;
import org.sharpler.hrofcrawler.parser.records.IllegalRecord;
import org.sharpler.hrofcrawler.parser.records.LoadClassRecord;
import org.sharpler.hrofcrawler.parser.records.StackFrameRecord;
import org.sharpler.hrofcrawler.parser.records.StackTraceRecord;
import org.sharpler.hrofcrawler.parser.records.StartThreadRecord;
import org.sharpler.hrofcrawler.parser.records.UnloadClassRecord;
import org.sharpler.hrofcrawler.parser.records.Utf8String;

import javax.annotation.Nullable;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.function.IntConsumer;
import java.util.function.LongConsumer;

public class HprofParserTest {
    @Test
    void illegalRecord() {
        var testCase = new Lazy<>(
                CheckedSupplier.unchecked(
                        () -> {
                            var handler = new TestHandler();
                            var parser = new HprofParser(handler);

                            var dump = new HeapDump(
                                    "kek",
                                    4,
                                    0,
                                    List.of(new IllegalRecord())
                            );

                            parser.parse(dump::toStream);

                            return handler.getDump();
                        }
                )
        );

        Assertions.assertThrows(
                UncheckedIOException.class,
                testCase::get,
                () -> String.format("Illegal record don't even throw an exception and test case return value: %s", testCase.get())
        );
    }

    @Test
    void parse() throws IOException {
        var handler = new TestHandler();

        var parser = new HprofParser(handler);

        var identifierSize = 4;

        var dump = new HeapDump(
                "kek",
                identifierSize,
                0,
                List.of(
                        new Utf8String(identifierSize, 1, "test string"),
                        new LoadClassRecord(0, 2, 1, 1, identifierSize),
                        new UnloadClassRecord(0),
                        new Utf8String(identifierSize, 3, "Main.java"),
                        new Utf8String(identifierSize, 4, "method()"),
                        new StackFrameRecord(0, 4, 4, 3, 0, 1, identifierSize),
                        new StackTraceRecord(1, 0, 3, new long[]{0}, identifierSize),
                        new HeapSummary(12345, 123, 1234567, 123456),
                        new StartThreadRecord(0, 5, 1, 1, 2, 3, identifierSize),
                        new EndThreadRecord(0),
                        new CpuSamples(1000, new CPUSample[]{new CPUSample(60, 1)}),
                        new ControlSettings(-1, (short) 256)
                )
        );

        parser.parse(dump::toStream);
    }

    static void consumeId(int identifierSize, long id, IntConsumer intConsumer, LongConsumer longConsumer) {
        switch (identifierSize) {
            case 4:
                intConsumer.accept(Math.toIntExact(id));
                break;
            case 8:
                longConsumer.accept(id);
                break;
            default:
                throw new IllegalArgumentException("Invalid identifierSize: " + identifierSize);
        }
    }

    public static void storeIntoStream(int identifierSize, long id, DataOutputStream output) throws IOException {
        switch (identifierSize) {
            case 4:
                output.writeInt(Math.toIntExact(id));
                break;
            case 8:
                output.writeLong(id);
                break;
            default:
                throw new IllegalArgumentException("Invalid identifierSize: " + identifierSize);
        }
    }

    static void storeIntoBuffer(int identifierSize, long id, ByteBuffer buffer) {
        consumeId(identifierSize, id, buffer::putInt, buffer::putLong);
    }

    private static final class TestHandler implements RecordHandler {
        @Nullable
        private HeapDump dump = null;

        @Nullable
        public HeapDump getDump() {
            return dump;
        }

        public HeapDump getDumpOrThrow() {
            return Objects.requireNonNull(dump);
        }

        @Override
        public void header(String format, int idSize, long time) {
            if (dump != null) {
                throw new IllegalStateException("Already set");
            }
            dump = new HeapDump(format, idSize, time, new ArrayList<>());
        }

        @Override
        public void stringInUTF8(long id, String data) {
            getDumpOrThrow().getRecords().add(
                    new Utf8String(getDumpOrThrow().getIdentifierSize(), id, data)
            );
        }

        @Override
        public void loadClass(int classSerialNum, long classObjId, int stackTraceSerialNum, long classNameStringId) {
            getDumpOrThrow().getRecords().add(
                    new LoadClassRecord(
                            classSerialNum,
                            classObjId,
                            stackTraceSerialNum,
                            classNameStringId,
                            getDumpOrThrow().getIdentifierSize()
                    )
            );
        }

        @Override
        public void unloadClass(int classSerialNum) {
            getDumpOrThrow().getRecords().add(
                    new UnloadClassRecord(classSerialNum)
            );
        }

        @Override
        public void stackFrame(
                long stackFrameId,
                long methodNameStringId,
                long methodSigStringId,
                long sourceFileNameStringId,
                int classSerialNum,
                int location
        ) {
            getDumpOrThrow().getRecords().add(
                    new StackFrameRecord(
                            stackFrameId,
                            methodNameStringId,
                            methodSigStringId,
                            sourceFileNameStringId,
                            classSerialNum,
                            location,
                            getDumpOrThrow().getIdentifierSize()
                    )
            );
        }

        @Override
        public void stackTrace(int stackTraceSerialNum, int threadSerialNum, int numFrames, long[] stackFrameIds) {
            getDumpOrThrow().getRecords().add(
                    new StackTraceRecord(
                            stackTraceSerialNum, threadSerialNum, numFrames, stackFrameIds, getDumpOrThrow().getIdentifierSize()
                    )
            );
        }

        @Override
        public void allocSites(
                short bitMaskFlags,
                float cutoffRatio,
                int totalLiveBytes,
                int totalLiveInstances,
                long totalBytesAllocated,
                long totalInstancesAllocated,
                AllocSite[] sites
        ) {
            System.out.printf(
                    "allocSites: bitMaskFlags=%d, cutoffRatio=%f, totalLiveBytes=%d, totalLiveInstances=%d, totalBytesAllocated=%d, totalInstancesAllocated=%d, sites=%s%n",
                    bitMaskFlags, cutoffRatio, totalLiveBytes, totalLiveInstances, totalBytesAllocated, totalInstancesAllocated, Arrays.toString(sites)
            );
        }

        @Override
        public void heapSummary(int totalLiveBytes, int totalLiveInstances, long totalBytesAllocated, long totalInstancesAllocated) {
            getDumpOrThrow().getRecords().add(
                    new HeapSummary(
                            totalLiveBytes, totalLiveInstances, totalBytesAllocated, totalInstancesAllocated
                    )
            );
        }

        @Override
        public void startThread(
                int threadSerialNum,
                long threadObjectId,
                int stackTraceSerialNum,
                long threadNameStringId,
                long threadGroupNameId,
                long threadParentGroupNameId
        ) {
            getDumpOrThrow().getRecords().add(
                    new StartThreadRecord(
                            threadSerialNum,
                            threadObjectId,
                            stackTraceSerialNum,
                            threadNameStringId,
                            threadGroupNameId,
                            threadParentGroupNameId,
                            getDumpOrThrow().getIdentifierSize()
                    )
            );
        }

        @Override
        public void endThread(int threadSerialNum) {
            getDumpOrThrow().getRecords().add(
                    new EndThreadRecord(threadSerialNum)
            );
        }

        @Override
        public void heapDump() {
            System.out.println("heapDump");
        }

        @Override
        public void heapDumpEnd() {
            System.out.println("heapDumpEnd");
        }

        @Override
        public void heapDumpSegment() {
            System.out.println("heapDumpSegment");
        }

        @Override
        public void cpuSamples(int totalNumOfSamples, CPUSample[] samples) {
            getDumpOrThrow().getRecords().add(new CpuSamples(totalNumOfSamples, samples));
        }

        @Override
        public void controlSettings(int bitMaskFlags, short stackTraceDepth) {
            getDumpOrThrow().getRecords().add(new ControlSettings(bitMaskFlags, stackTraceDepth));
        }

        @Override
        public void rootUnknown(long objId) {
            System.out.printf("rootUnknown: objId=%d%n", objId);
        }

        @Override
        public void rootJNIGlobal(long objId, long JNIGlobalRefId) {
            System.out.printf("rootJNIGlobal: objId=%d, JNIGlobalRefId=%d%n", objId, JNIGlobalRefId);
        }

        @Override
        public void rootJNILocal(long objId, int threadSerialNum, int frameNum) {
            System.out.println("rootJNILocal");
        }

        @Override
        public void rootJavaFrame(long objId, int threadSerialNum, int frameNum) {
            System.out.println("rootJavaFrame");
        }

        @Override
        public void rootNativeStack(long objId, int threadSerialNum) {
            System.out.println("rootNativeStack");
        }

        @Override
        public void rootStickyClass(long objId) {
            System.out.println("rootStickyClass");
        }

        @Override
        public void rootThreadBlock(long objId, int threadSerialNum) {
            System.out.println("rootThreadBlock");
        }

        @Override
        public void rootMonitorUsed(long objId) {
            System.out.println("rootMonitorUsed");
        }

        @Override
        public void rootThreadObj(long objId, int threadSerialNum, int stackTraceSerialNum) {
            System.out.println("rootThreadObj");
        }

        @Override
        public void classDump(long classObjId, int stackTraceSerialNum, long superClassObjId, long classLoaderObjId, long signersObjId, long protectionDomainObjId, long reserved1, long reserved2, int instanceSize, Constant[] constants, Static[] statics, InstanceField[] instanceFields) {
            System.out.println("classDump");
        }

        @Override
        public void instanceDump(long objId, int stackTraceSerialNum, long classObjId, List<Value> instanceFieldValues) {
            System.out.println("instanceDump");
        }

        @Override
        public void objArrayDump(long objId, int stackTraceSerialNum, long elemClassObjId, long[] elems) {
            System.out.println("objArrayDump");
        }

        @Override
        public void primArrayDump(long objId, int stackTraceSerialNum, PrimArray array) {
            System.out.println("primArrayDump");
        }

        @Override
        public void finished() {
            System.out.println("finished");
        }
    }
}