package org.sharpler.hprofcrawler.dbs;

import org.rocksdb.ColumnFamilyHandle;
import org.rocksdb.RocksDB;
import org.sharpler.hprofcrawler.Utils;

import javax.annotation.Nullable;

/**
 * Map: stringId -> {@link String}.
 */
public final class NamesDb extends Database {
    public NamesDb(RocksDB db, ColumnFamilyHandle handle) {
        super(db, handle);
    }

    public void put(long id, String name) {
        put(Utils.serializeLong(id), name.getBytes());
    }

    @Nullable
    public String find(long id) {
        return Utils.map(find(Utils.serializeLong(id)), String::new);
    }
}
