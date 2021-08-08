package org.sharpler.hprofcrawler.dbs;

import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import org.rocksdb.ColumnFamilyHandle;
import org.rocksdb.RocksDB;
import org.sharpler.hprofcrawler.Utils;
import org.sharpler.hprofcrawler.api.ClassFilter;
import org.sharpler.hprofcrawler.views.ClassView;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class ClassInfoDb extends Database {

    public ClassInfoDb(RocksDB db, ColumnFamilyHandle handle) {
        super(db, handle);
    }

    public void put(long classId, ClassView classView) {
        put(Utils.serializeLong(classId), classView.serialize());
    }

    public Long2ObjectOpenHashMap<ClassView> reloadIndex() {
        var result = new Long2ObjectOpenHashMap<ClassView>();
        try (var iterator = iterator()) {
            for (iterator.seekToFirst(); iterator.isValid(); iterator.next()) {
                result.put(
                        Utils.deserializeLong(iterator.key()),
                        ClassView.deserialize(iterator.value())
                );
            }

            return result;
        }
    }

    public List<ClassView> find(ClassFilter filter) {
        var result = new ArrayList<ClassView>();
        try (var iterator = iterator()) {
            for (iterator.seekToFirst(); iterator.isValid(); iterator.next()) {
                if (filter.filterId(Utils.deserializeLong(iterator.key()))) {
                    var classView = ClassView.deserialize(iterator.value());
                    if (filter.filterClass(classView)) {
                        result.add(classView);
                    }
                }
            }

            return result;
        }
    }

    @Nullable
    public ClassView find(long id) {
        return Utils.map(find(Utils.serializeLong(id)), ClassView::deserialize);
    }
}
