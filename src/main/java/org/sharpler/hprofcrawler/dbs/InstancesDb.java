package org.sharpler.hprofcrawler.dbs;

import org.rocksdb.ColumnFamilyHandle;
import org.rocksdb.RocksDB;
import org.sharpler.hprofcrawler.Utils;
import org.sharpler.hprofcrawler.entries.InstanceEntry;
import org.sharpler.hprofcrawler.parser.Instance;
import org.sharpler.hprofcrawler.views.ClassView;
import org.sharpler.hprofcrawler.views.InstanceView;

import java.util.function.Predicate;

/**
 * Map: (classId, objectId) -> {@link InstanceEntry}.
 */
public final class InstancesDb extends Database {
    public InstancesDb(RocksDB db, ColumnFamilyHandle handle) {
        super(db, handle);
    }

    public void put(Instance x) {
        put(Utils.serializeTwoLong(x.classObjId, x.objId), x.serialize());
    }

    public void scan(ClassView classView, Predicate<? super InstanceView> consumer) {
        try (var iterator = iterator()) {
            for (iterator.seek(Utils.serializeLong(classView.getId())); iterator.isValid(); iterator.next()) {
                if (Utils.deserializeLong(iterator.key()) != classView.getId()) {
                    break;
                }

                if (consumer.test(Instance.deserialize(iterator.value(), classView))) {
                    break;
                }
            }

        }
    }
}
