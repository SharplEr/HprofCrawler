package org.sharpler.hrofcrawler.views;

import java.util.List;

import org.sharpler.hrofcrawler.entries.InstanceEntry;
import org.sharpler.hrofcrawler.backend.Index;
import org.sharpler.hrofcrawler.parser.Value;

public final class InstanceView {
    private final long objId;
    private final ClassView classView;

    private final List<Value> fields;

    public InstanceView(long objId, ClassView classView, List<Value> fields) {
        this.objId = objId;
        this.classView = classView;
        this.fields = fields;
    }

    public static InstanceView of(InstanceEntry entry, Index index) {
        return new InstanceView(
                entry.getObjectId(),
                index.findClassView(entry.getClassId()),
                entry.getFields()
        );
    }

    public InstanceEntry toInstanceEntry() {
        return new InstanceEntry(objId, classView.getId(), fields);
    }

    public long getObjId() {
        return objId;
    }

    public ClassView getClassView() {
        return classView;
    }

    public List<Value> getFields() {
        return fields;
    }
}
