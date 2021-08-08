package org.sharpler.hprofcrawler.dbs;

import org.rocksdb.ColumnFamilyHandle;
import org.rocksdb.RocksDB;
import org.sharpler.hprofcrawler.Utils;

import java.util.OptionalLong;

/**
 * Map: objectId->classId.
 */
public final class Object2ClassDb extends Database {

    public Object2ClassDb(RocksDB db, ColumnFamilyHandle handle) {
        super(db, handle);
    }

    public void put(long objectId, long classId) {
        put(Utils.serializeLong(objectId), Utils.serializeLong(classId));
    }

    public OptionalLong findClassId(long objectId) {
        byte[] classId = find(Utils.serializeLong(objectId));
        return classId == null ? OptionalLong.empty() : OptionalLong.of(Utils.deserializeLong(classId));
    }
}

