package org.sharpler.hprofcrawler.parser;

import org.sharpler.hprofcrawler.views.ClassView;
import org.sharpler.hprofcrawler.views.InstanceView;

import java.nio.ByteBuffer;

public class Instance {
    public final long objId;
    public final int stackTraceSerialNum;
    public final long classObjId;
    public final byte[] packedValues;

    public Instance(
            long objId,
            int stackTraceSerialNum,
            long classObjId,
            byte[] packedValues
    ) {
        this.objId = objId;
        this.stackTraceSerialNum = stackTraceSerialNum;
        this.classObjId = classObjId;
        this.packedValues = packedValues;
    }

    public byte[] serialize() {
        var buffer = ByteBuffer.allocate(2 * Long.SIZE + Integer.SIZE + packedValues.length);
        buffer.putLong(classObjId);
        buffer.putLong(objId);
        buffer.putInt(stackTraceSerialNum);
        buffer.put(packedValues);

        return buffer.array();
    }

    public static InstanceView deserialize(byte[] data, ClassView classView) {
        var buffer = ByteBuffer.wrap(data);
        var classId = buffer.getLong();
        var objectId = buffer.getLong();
        var stackTraceSerialNum = buffer.getInt();
        return new InstanceView(objectId, classView, classView.deserializePackedFieldValues(buffer));
    }
}
