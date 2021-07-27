package org.sharpler.hprofcrawler.views;

import org.sharpler.hprofcrawler.dbs.ClassInfoDb;
import org.sharpler.hprofcrawler.entries.InstanceEntry;
import org.sharpler.hprofcrawler.parser.Value;

import java.util.List;
import java.util.Objects;

public final class InstanceView {
    private final long objId;
    private final ClassView classView;

    private final List<Value> fields;

    public InstanceView(long objId, ClassView classView, List<Value> fields) {
        this.objId = objId;
        this.classView = classView;
        this.fields = fields;
    }

    public static InstanceView of(InstanceEntry entry, ClassView classView) {
        return new InstanceView(
                entry.getObjectId(),
                classView,
                entry.getFields()
        );
    }

    public static InstanceView of(InstanceEntry entry, ClassInfoDb classes) {
        return new InstanceView(
                entry.getObjectId(),
                Objects.requireNonNull(classes.find(entry.getClassId())),
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
