package org.sharpler.hrofcrawler.parser;

import java.util.List;

public class DummyHandler implements RecordHandler {
    @Override
    public void header(String format, int idSize, long time) {
        // No-op.
    }

    @Override
    public void stringInUTF8(long id, String data) {
        // No-op.
    }

    @Override
    public void loadClass(int classSerialNum, long classObjId, int stackTraceSerialNum, long classNameStringId) {
        // No-op.
    }

    @Override
    public void unloadClass(int classSerialNum) {
        // No-op.
    }

    @Override
    public void stackFrame(long stackFrameId, long methodNameStringId, long methodSigStringId,
                           long sourceFileNameStringId, int classSerialNum, int location) {
        // No-op.
    }

    @Override
    public void stackTrace(int stackTraceSerialNum, int threadSerialNum, int numFrames, long[] stackFrameIds) {
        // No-op.
    }

    @Override
    public void allocSites(short bitMaskFlags, float cutoffRatio, int totalLiveBytes, int totalLiveInstances,
                           long totalBytesAllocated, long totalInstancesAllocated, AllocSite[] sites) {
        // No-op.
    }

    @Override
    public void heapSummary(int totalLiveBytes, int totalLiveInstances, long totalBytesAllocated,
                            long totalInstancesAllocated) {
        // No-op.
    }

    @Override
    public void startThread(int threadSerialNum, long threadObjectId, int stackTraceSerialNum, long threadNameStringId,
                            long threadGroupNameId, long threadParentGroupNameId) {
        // No-op.
    }

    @Override
    public void endThread(int threadSerialNum) {
        // No-op.
    }

    @Override
    public void heapDump() {
        // No-op.
    }

    @Override
    public void heapDumpEnd() {
        // No-op.
    }

    @Override
    public void heapDumpSegment() {
        // No-op.
    }

    @Override
    public void cpuSamples(int totalNumOfSamples, CPUSample[] samples) {
        // No-op.
    }

    @Override
    public void controlSettings(int bitMaskFlags, short stackTraceDepth) {
        // No-op.
    }

    @Override
    public void rootUnknown(long objId) {
        // No-op.
    }

    @Override
    public void rootJNIGlobal(long objId, long JNIGlobalRefId) {
        // No-op.
    }

    @Override
    public void rootJNILocal(long objId, int threadSerialNum, int frameNum) {

    }

    @Override
    public void rootJavaFrame(long objId, int threadSerialNum, int frameNum) {
        // No-op.
    }

    @Override
    public void rootNativeStack(long objId, int threadSerialNum) {
        // No-op.
    }

    @Override
    public void rootStickyClass(long objId) {
        // No-op.
    }

    @Override
    public void rootThreadBlock(long objId, int threadSerialNum) {
        // No-op.
    }

    @Override
    public void rootMonitorUsed(long objId) {
        // No-op.
    }

    @Override
    public void rootThreadObj(long objId, int threadSerialNum, int stackTraceSerialNum) {
        // No-op.
    }

    @Override
    public void classDump(
            long classObjId,
            int stackTraceSerialNum,
            long superClassObjId,
            long classLoaderObjId,
            long signersObjId,
            long protectionDomainObjId,
            long reserved1,
            long reserved2,
            int instanceSize,
            Constant[] constants,
            Static[] statics,
            InstanceField[] instanceFields) {
        // No-op.
    }

    @Override
    public void instanceDump(long objId, int stackTraceSerialNum, long classObjId, List<Value> instanceFieldValues) {
        // No-op.
    }

    @Override
    public void objArrayDump(long objId, int stackTraceSerialNum, long elemClassObjId, long[] elems) {
        // No-op.
    }

    @Override
    public void primArrayDump(long objId, int stackTraceSerialNum, PrimArray array) {
        // No-op.
    }

    @Override
    public void finished() {
        // No-op.
    }
}
