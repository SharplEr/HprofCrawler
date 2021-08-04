package org.sharpler.hprofcrawler.parser;

public interface RecordHandler {
    void header(String format, int idSize, long time);

    void stringInUTF8(long id, String data);

    void loadClass(int classSerialNum, long classObjId, int stackTraceSerialNum, long classNameStringId);

    void unloadClass(int classSerialNum);

    void stackFrame(long stackFrameId,
                    long methodNameStringId,
                    long methodSigStringId,
                    long sourceFileNameStringId,
                    int classSerialNum,
                    int location);

    void stackTrace(int stackTraceSerialNum, int threadSerialNum, int numFrames, long[] stackFrameIds);

    void allocSites(short bitMaskFlags,
                    float cutoffRatio,
                    int totalLiveBytes,
                    int totalLiveInstances,
                    long totalBytesAllocated,
                    long totalInstancesAllocated,
                    AllocSite[] sites);

    void heapSummary(
            int totalLiveBytes,
            int totalLiveInstances,
            long totalBytesAllocated,
            long totalInstancesAllocated);

    void startThread(int threadSerialNum,
                     long threadObjectId,
                     int stackTraceSerialNum,
                     long threadNameStringId,
                     long threadGroupNameId,
                     long threadParentGroupNameId);

    void endThread(int threadSerialNum);

    void heapDump();

    void heapDumpEnd();

    void heapDumpSegment();

    void cpuSamples(int totalNumOfSamples, CPUSample[] samples);

    void controlSettings(int bitMaskFlags, short stackTraceDepth);

    void rootUnknown(long objId);

    void rootJNIGlobal(long objId, long JNIGlobalRefId);

    void rootJNILocal(long objId, int threadSerialNum, int frameNum);

    void rootJavaFrame(long objId, int threadSerialNum, int frameNum);

    void rootNativeStack(long objId, int threadSerialNum);

    void rootStickyClass(long objId);

    void rootThreadBlock(long objId, int threadSerialNum);

    void rootMonitorUsed(long objId);

    void rootThreadObj(long objId, int threadSerialNum, int stackTraceSerialNum);

    void classDump(long classObjId,
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
                   InstanceField[] instanceFields);

    void instanceDump(Instance instance);

    void objArrayDump(long objId, int stackTraceSerialNum, long elemClassObjId, long[] elems);

    void primArrayDump(long objId, int stackTraceSerialNum, PrimArray array);

    void finished();
}
