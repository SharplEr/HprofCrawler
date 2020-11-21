package org.sharpler.hrofcrawler.parser;

import java.util.List;

public class DummyHandler implements RecordHandler{
    @Override
    public void header(String format, int idSize, long time) {

    }

    @Override
    public void stringInUTF8(long id, String data) {

    }

    @Override
    public void loadClass(int classSerialNum, long classObjId, int stackTraceSerialNum, long classNameStringId) {

    }

    @Override
    public void unloadClass(int classSerialNum) {

    }

    @Override
    public void stackFrame(long stackFrameId, long methodNameStringId, long methodSigStringId,
                           long sourceFileNameStringId, int classSerialNum, int location)
    {

    }

    @Override
    public void stackTrace(int stackTraceSerialNum, int threadSerialNum, int numFrames, long[] stackFrameIds) {

    }

    @Override
    public void allocSites(short bitMaskFlags, float cutoffRatio, int totalLiveBytes, int totalLiveInstances,
                           long totalBytesAllocated, long totalInstancesAllocated, AllocSite[] sites)
    {

    }

    @Override
    public void heapSummary(int totalLiveBytes, int totalLiveInstances, long totalBytesAllocated,
                            long totalInstancesAllocated)
    {

    }

    @Override
    public void startThread(int threadSerialNum, long threadObjectId, int stackTraceSerialNum, long threadNameStringId,
                            long threadGroupNameId, long threadParentGroupNameId)
    {

    }

    @Override
    public void endThread(int threadSerialNum) {

    }

    @Override
    public void heapDump() {

    }

    @Override
    public void heapDumpEnd() {

    }

    @Override
    public void heapDumpSegment() {

    }

    @Override
    public void cpuSamples(int totalNumOfSamples, CPUSample[] samples) {

    }

    @Override
    public void controlSettings(int bitMaskFlags, short stackTraceDepth) {

    }

    @Override
    public void rootUnknown(long objId) {

    }

    @Override
    public void rootJNIGlobal(long objId, long JNIGlobalRefId) {

    }

    @Override
    public void rootJNILocal(long objId, int threadSerialNum, int frameNum) {

    }

    @Override
    public void rootJavaFrame(long objId, int threadSerialNum, int frameNum) {

    }

    @Override
    public void rootNativeStack(long objId, int threadSerialNum) {

    }

    @Override
    public void rootStickyClass(long objId) {

    }

    @Override
    public void rootThreadBlock(long objId, int threadSerialNum) {

    }

    @Override
    public void rootMonitorUsed(long objId) {

    }

    @Override
    public void rootThreadObj(long objId, int threadSerialNum, int stackTraceSerialNum) {

    }

    @Override
    public void classDump(long classObjId, int stackTraceSerialNum, long superClassObjId, long classLoaderObjId,
                          long signersObjId, long protectionDomainObjId, long reserved1, long reserved2, int instanceSize,
                          Constant[] constants, Static[] statics, InstanceField[] instanceFields)
    {

    }

    @Override
    public void instanceDump(long objId, int stackTraceSerialNum, long classObjId, List<Value> instanceFieldValues) {

    }

    @Override
    public void objArrayDump(long objId, int stackTraceSerialNum, long elemClassObjId, long[] elems) {

    }

    @Override
    public void primArrayDump(long objId, int stackTraceSerialNum, PrimArray array) {

    }

    @Override
    public void finished() {

    }
}
