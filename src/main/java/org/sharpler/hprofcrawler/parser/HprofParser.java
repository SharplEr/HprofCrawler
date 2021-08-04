package org.sharpler.hprofcrawler.parser;

import java.io.BufferedInputStream;
import java.io.DataInput;
import java.io.DataInputStream;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public final class HprofParser {

    private final RecordHandler handler;

    public HprofParser(RecordHandler handler) {
        this.handler = handler;
    }

    public void parse(File file) throws IOException {

        /* The file format looks like this:
         *
         * header:
         *   [u1]* - a null-terminated sequence of bytes representing the format
         *           name and version
         *   u4 - size of identifiers/pointers
         *   u4 - high number of word of number of milliseconds since 0:00 GMT,
         *        1/1/70
         *   u4 - low number of word of number of milliseconds since 0:00 GMT,
         *        1/1/70
         *
         * records:
         *   u1 - tag denoting the type of record
         *   u4 - number of microseconds since timestamp in header
         *   u4 - number of bytes that follow this field in this record
         *   [u1]* - body
         */

        FileInputStream fs = new FileInputStream(file);
        DataInputStream in = new DataInputStream(new BufferedInputStream(fs, 16 * 1024));

        // header
        String format = readUntilNull(in);
        int idSize = in.readInt();
        long startTime = in.readLong();
        handler.header(format, idSize, startTime);

        // records
        boolean done;
        do {
            done = parseRecord(in, idSize);
        } while (!done);
        in.close();
        
        handler.finished();
    }

    public static String readUntilNull(DataInput in) throws IOException {

        int bytesRead = 0;
        byte[] bytes = new byte[25];

        while ((bytes[bytesRead] = in.readByte()) != 0) {
            bytesRead++;
            if (bytesRead >= bytes.length) {
                byte[] newBytes = new byte[bytesRead + 20];
                System.arraycopy(bytes, 0, newBytes, 0, bytes.length);
                bytes = newBytes;
            }
        }
        return new String(bytes, 0, bytesRead);
    }

    /**
     * @return true if there are no more records to parse
     */
    private boolean parseRecord(DataInput in, int idSize) throws IOException {

        /* format:
         *   u1 - tag
         *   u4 - time
         *   u4 - length
         *   [u1]* - body
         */

        // if we get an EOF on this read, it just means we're done
        byte tag;
        try {
            tag = in.readByte();
        } catch (EOFException e) {
            return true;
        }

        // Otherwise, propagate the EOFException
        int time = in.readInt();    // TODO(eaftan): we might want time passed to handler fns
        long bytesLeft = Integer.toUnsignedLong(in.readInt());

        long l1, l2, l3, l4;
        int i1, i2, i3, i4, i5, i6, i7, i8, i9;
        short s1;
        byte b1;
        float f1;
        byte[] bArr1;
        long[] lArr1;

        switch (tag) {
            case 0x1:
                // String in UTF-8
                l1 = readId(idSize, in);
                bytesLeft -= idSize;
                bArr1 = new byte[Math.toIntExact(bytesLeft)];
                in.readFully(bArr1);
                handler.stringInUTF8(l1, new String(bArr1));
                break;

            case 0x2:
                // Load class
                i1 = in.readInt();
                l1 = readId(idSize, in);
                i2 = in.readInt();
                l2 = readId(idSize, in);
                handler.loadClass(i1, l1, i2, l2);
                break;

            case 0x3:
                // Unload class
                i1 = in.readInt();
                handler.unloadClass(i1);
                break;

            case 0x4:
                // Stack frame
                l1 = readId(idSize, in);
                l2 = readId(idSize, in);
                l3 = readId(idSize, in);
                l4 = readId(idSize, in);
                i1 = in.readInt();
                i2 = in.readInt();
                handler.stackFrame(l1, l2, l3, l4, i1, i2);
                break;

            case 0x5:
                // Stack trace
                i1 = in.readInt();
                i2 = in.readInt();
                i3 = in.readInt();
                bytesLeft -= 12;
                lArr1 = new long[Math.toIntExact(bytesLeft / idSize)];
                for (int i = 0; i < lArr1.length; i++) {
                    lArr1[i] = readId(idSize, in);
                }
                handler.stackTrace(i1, i2, i3, lArr1);
                break;

            case 0x6:
                // Alloc sites
                s1 = in.readShort();
                f1 = in.readFloat();
                i1 = in.readInt();
                i2 = in.readInt();
                l1 = in.readLong();
                l2 = in.readLong();
                i3 = in.readInt();    // num of sites that follow

                AllocSite[] allocSites = new AllocSite[i3];
                for (int i = 0; i < allocSites.length; i++) {
                    b1 = in.readByte();
                    i4 = in.readInt();
                    i5 = in.readInt();
                    i6 = in.readInt();
                    i7 = in.readInt();
                    i8 = in.readInt();
                    i9 = in.readInt();

                    allocSites[i] = new AllocSite(b1, i4, i5, i6, i7, i8, i9);
                }
                handler.allocSites(s1, f1, i1, i2, l1, l2, allocSites);
                break;

            case 0x7:
                // Heap summary
                i1 = in.readInt();
                i2 = in.readInt();
                l1 = in.readLong();
                l2 = in.readLong();
                handler.heapSummary(i1, i2, l1, l2);
                break;

            case 0xa:
                // Start thread
                i1 = in.readInt();
                l1 = readId(idSize, in);
                i2 = in.readInt();
                l2 = readId(idSize, in);
                l3 = readId(idSize, in);
                l4 = readId(idSize, in);
                handler.startThread(i1, l1, i2, l2, l3, l4);
                break;

            case 0xb:
                // End thread
                i1 = in.readInt();
                handler.endThread(i1);
                break;

            case 0xc:
                // Heap dump
                handler.heapDump();
                while (bytesLeft > 0) {
                    bytesLeft -= parseHeapDump(in, idSize);
                }
                handler.heapDumpEnd();
                break;

            case 0x1c:
                // Heap dump segment
                handler.heapDumpSegment();
                while (bytesLeft > 0) {
                    bytesLeft -= parseHeapDump(in, idSize);
                }
                break;

            case 0x2c:
                // Heap dump end (of segments)
                handler.heapDumpEnd();
                break;

            case 0xd:
                // CPU samples
                i1 = in.readInt();
                i2 = in.readInt();    // num samples that follow

                CPUSample[] samples = new CPUSample[i2];
                for (int i = 0; i < samples.length; i++) {
                    i3 = in.readInt();
                    i4 = in.readInt();
                    samples[i] = new CPUSample(i3, i4);
                }
                handler.cpuSamples(i1, samples);
                break;

            case 0xe:
                // Control settings
                i1 = in.readInt();
                s1 = in.readShort();
                handler.controlSettings(i1, s1);
                break;

            default:
                throw new IllegalArgumentException("Unexpected top-level record type: " + tag);
        }

        return false;
    }

    // returns number of bytes parsed
    private int parseHeapDump(DataInput in, int idSize) throws IOException {

        byte tag = in.readByte();
        int bytesRead = 1;

        long l1, l2, l3, l4, l5, l6, l7;
        int i1, i2;
        short s1, s2, s3;
        byte b1;
        byte[] bArr1;
        long[] lArr1;

        switch (tag) {

            case -1:    // 0xFF
                // Root unknown
                l1 = readId(idSize, in);
                handler.rootUnknown(l1);
                bytesRead += idSize;
                break;

            case 0x01:
                // Root JNI global
                l1 = readId(idSize, in);
                l2 = readId(idSize, in);
                handler.rootJNIGlobal(l1, l2);
                bytesRead += 2 * idSize;
                break;

            case 0x02:
                // Root JNI local
                l1 = readId(idSize, in);
                i1 = in.readInt();
                i2 = in.readInt();
                handler.rootJNILocal(l1, i1, i2);
                bytesRead += idSize + 8;
                break;

            case 0x03:
                // Root Java frame
                l1 = readId(idSize, in);
                i1 = in.readInt();
                i2 = in.readInt();
                handler.rootJavaFrame(l1, i1, i2);
                bytesRead += idSize + 8;
                break;

            case 0x04:
                // Root native stack
                l1 = readId(idSize, in);
                i1 = in.readInt();
                handler.rootNativeStack(l1, i1);
                bytesRead += idSize + 4;
                break;

            case 0x05:
                // Root sticky class
                l1 = readId(idSize, in);
                handler.rootStickyClass(l1);
                bytesRead += idSize;
                break;

            case 0x06:
                // Root thread block
                l1 = readId(idSize, in);
                i1 = in.readInt();
                handler.rootThreadBlock(l1, i1);
                bytesRead += idSize + 4;
                break;

            case 0x07:
                // Root monitor used
                l1 = readId(idSize, in);
                handler.rootMonitorUsed(l1);
                bytesRead += idSize;
                break;

            case 0x08:
                // Root thread object
                l1 = readId(idSize, in);
                i1 = in.readInt();
                i2 = in.readInt();
                handler.rootThreadObj(l1, i1, i2);
                bytesRead += idSize + 8;
                break;

            case 0x20:
                // Class dump
                l1 = readId(idSize, in);
                i1 = in.readInt();
                l2 = readId(idSize, in);
                l3 = readId(idSize, in);
                l4 = readId(idSize, in);
                l5 = readId(idSize, in);
                l6 = readId(idSize, in);
                l7 = readId(idSize, in);
                i2 = in.readInt();
                bytesRead += idSize * 7 + 8;

                /* Constants */
                s1 = in.readShort();    // number of constants
                bytesRead += 2;
                assert s1 >= 0;

                Constant[] constants = new Constant[s1];
                for (int i = 0; i < s1; i++) {
                    short constantPoolIndex = in.readShort();
                    byte btype = in.readByte();
                    bytesRead += 3;
                    Type type = Type.hprofTypeToEnum(btype);
                    Value v = null;

                    switch (type) {
                        case OBJ:
                            long vid = readId(idSize, in);
                            bytesRead += idSize;
                            v = Value.ofLong(vid, true);
                            break;
                        case BOOL:
                            boolean vbool = in.readBoolean();
                            bytesRead += 1;
                            v = Value.ofBool(vbool);
                            break;
                        case CHAR:
                            char vc = in.readChar();
                            bytesRead += 2;
                            v = Value.ofChar(vc);
                            break;
                        case FLOAT:
                            float vf = in.readFloat();
                            bytesRead += 4;
                            v = Value.ofFloat(vf);
                            break;
                        case DOUBLE:
                            double vd = in.readDouble();
                            bytesRead += 8;
                            v = Value.ofDouble(vd);
                            break;
                        case BYTE:
                            byte vbyte = in.readByte();
                            bytesRead += 1;
                            v = Value.ofByte(vbyte);
                            break;
                        case SHORT:
                            short vs = in.readShort();
                            bytesRead += 2;
                            v = Value.ofShort(vs);
                            break;
                        case INT:
                            int vi = in.readInt();
                            bytesRead += 4;
                            v = Value.ofInt(vi);
                            break;
                        case LONG:
                            long vl = in.readLong();
                            bytesRead += 8;
                            v = Value.ofLong(vl, false);
                            break;
                    }

                    constants[i] = new Constant(constantPoolIndex, v);
                }

                /* Statics */
                s2 = in.readShort();    // number of static fields
                bytesRead += 2;
                assert s2 >= 0;

                Static[] statics = new Static[s2];
                for (int i = 0; i < s2; i++) {
                    long staticFieldNameStringId = readId(idSize, in);
                    byte btype = in.readByte();
                    bytesRead += idSize + 1;
                    Type type = Type.hprofTypeToEnum(btype);
                    Value v = null;

                    switch (type) {
                        case OBJ:     // object
                            long vid = readId(idSize, in);
                            bytesRead += idSize;
                            v = Value.ofLong(vid, true);
                            break;
                        case BOOL:     // boolean
                            boolean vbool = in.readBoolean();
                            bytesRead += 1;
                            v = Value.ofBool(vbool);
                            break;
                        case CHAR:     // char
                            char vc = in.readChar();
                            bytesRead += 2;
                            v = Value.ofChar(vc);
                            break;
                        case FLOAT:     // float
                            float vf = in.readFloat();
                            bytesRead += 4;
                            v = Value.ofFloat(vf);
                            break;
                        case DOUBLE:     // double
                            double vd = in.readDouble();
                            bytesRead += 8;
                            v = Value.ofDouble(vd);
                            break;
                        case BYTE:     // byte
                            byte vbyte = in.readByte();
                            bytesRead += 1;
                            v = Value.ofByte(vbyte);
                            break;
                        case SHORT:     // short
                            short vs = in.readShort();
                            bytesRead += 2;
                            v = Value.ofShort(vs);
                            break;
                        case INT:    // int
                            int vi = in.readInt();
                            bytesRead += 4;
                            v = Value.ofInt(vi);
                            break;
                        case LONG:    // long
                            long vl = in.readLong();
                            bytesRead += 8;
                            v = Value.ofLong(vl, false);
                            break;
                    }

                    statics[i] = new Static(staticFieldNameStringId, v);
                }

                /* Instance fields */
                s3 = in.readShort();    // number of instance fields
                bytesRead += 2;
                assert s3 >= 0;

                InstanceField[] instanceFields = new InstanceField[s3];
                for (int i = 0; i < s3; i++) {
                    long fieldNameStringId = readId(idSize, in);
                    byte btype = in.readByte();
                    bytesRead += idSize + 1;
                    Type type = Type.hprofTypeToEnum(btype);
                    instanceFields[i] = new InstanceField(fieldNameStringId, type);
                }

                handler.classDump(l1, i1, l2, l3, l4, l5, l6, l7, i2, constants,
                        statics, instanceFields);
                break;

            case 0x21:
                // Instance dump
                l1 = readId(idSize, in);
                i1 = in.readInt();
                l2 = readId(idSize, in);    // class obj id
                i2 = in.readInt();    // num of bytes that follow

                assert i2 >= 0;

                bArr1 = new byte[i2];
                in.readFully(bArr1);

                handler.instanceDump(new Instance(l1, i1, l2, bArr1));

                bytesRead += idSize * 2 + 8 + i2;
                break;

            case 0x22:
                // Object array dump
                l1 = readId(idSize, in);
                i1 = in.readInt();
                i2 = in.readInt();    // number of elements
                l2 = readId(idSize, in);

                assert i2 >= 0;

                lArr1 = new long[i2];
                for (int i = 0; i < i2; i++) {
                    lArr1[i] = readId(idSize, in);
                }
                handler.objArrayDump(l1, i1, l2, lArr1);

                bytesRead += (2 + i2) * idSize + 8;
                break;

            case 0x23:
                // Primitive array dump
                l1 = readId(idSize, in);
                i1 = in.readInt();
                i2 = in.readInt();    // number of elements
                b1 = in.readByte();
                bytesRead += idSize + 9;

                assert i2 >= 0;

                // i2 -- length;
                Object vs = null;
                Type t = Type.hprofTypeToEnum(b1);
                switch (t) {
                    case OBJ:
                        throw new IllegalStateException("should be not objects");
                    case BOOL:
                        boolean[] bools = new boolean[i2];
                        for (int i = 0; i < i2; i++) {
                            bools[i] = in.readBoolean();
                            bytesRead += 1;
                        }
                        vs = bools;
                        break;
                    case CHAR:
                        char[] chars = new char[i2];
                        for (int i = 0; i < i2; i++) {
                            chars[i] = in.readChar();
                            bytesRead += 2;
                        }
                        vs = chars;
                        break;
                    case FLOAT:
                        float[] floats = new float[i2];
                        for (int i = 0; i < i2; i++) {
                            floats[i] = in.readFloat();
                            bytesRead += 4;
                        }
                        vs = floats;
                        break;
                    case DOUBLE:
                        double[] doubles = new double[i2];
                        for (int i = 0; i < i2; i++) {
                            doubles[i] = in.readDouble();
                            bytesRead += 8;
                        }
                        vs = doubles;
                        break;
                    case BYTE:
                        byte[] bytes = new byte[i2];
                        for (int i = 0; i < i2; i++) {
                            bytes[i] = in.readByte();
                            bytesRead += 1;
                        }
                        vs = bytes;
                        break;
                    case SHORT:
                        short[] shorts = new short[i2];
                        for (int i = 0; i < i2; i++) {
                            shorts[i] = in.readShort();
                            bytesRead += 2;
                        }
                        vs = shorts;
                        break;
                    case INT:
                        int[] ints = new int[i2];
                        for (int i = 0; i < i2; i++) {
                            ints[i] = in.readInt();
                            bytesRead += 4;
                        }
                        vs = ints;
                        break;
                    case LONG:
                        long[] longs = new long[i2];
                        for (int i = 0; i < i2; i++) {
                            longs[i] = in.readLong();
                            bytesRead += 8;
                        }
                        vs = longs;
                        break;
                }
                handler.primArrayDump(l1, i1, new PrimArray(t, l1, vs));
                break;

            default:
                throw new IllegalArgumentException("Unexpected heap dump sub-record type: " + tag);
        }

        return bytesRead;
    }

    private static long readId(int idSize, DataInput in) throws IOException {
        long id = -1;
        if (idSize == 4) {
            id = in.readInt();
            id &= 0x00000000ffffffff;     // undo sign extension
        } else if (idSize == 8) {
            id = in.readLong();
        } else {
            throw new IllegalArgumentException("Invalid identifier size " + idSize);
        }

        return id;
    }
}
