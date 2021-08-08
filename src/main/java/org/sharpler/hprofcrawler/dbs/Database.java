package org.sharpler.hprofcrawler.dbs;

import org.rocksdb.ColumnFamilyHandle;
import org.rocksdb.FlushOptions;
import org.rocksdb.RocksDB;
import org.rocksdb.RocksDBException;
import org.rocksdb.RocksIterator;
import org.sharpler.hprofcrawler.api.Progress;

import javax.annotation.Nullable;

public abstract class Database {
    protected final RocksDB db;
    protected final ColumnFamilyHandle handle;

    protected Database(RocksDB db, ColumnFamilyHandle handle) {
        this.db = db;
        this.handle = handle;
    }

    protected final void put(byte[] key, byte[] value) {
        try {
            db.put(handle, key, value);
        } catch (RocksDBException e) {
            throw new RuntimeException(e);
        }
    }

    @Nullable
    protected final byte[] find(byte[] key) {
        try {
            return db.get(key);
        } catch (RocksDBException e) {
            throw new RuntimeException(e);
        }
    }

    protected final RocksIterator iterator() {
        return db.newIterator(handle);
    }

    public final void compact() {
        try (var options = new FlushOptions().setWaitForFlush(true).setAllowWriteStall(true)) {
            db.flush(options, handle);
            db.compactRange(handle);
        } catch (RocksDBException e) {
            throw new RuntimeException(e);
        }
    }


    public static void compactAll(Progress progress, Database... databases) {
        for (int i = 0; i < databases.length; i++) {
            databases[i].compact();
            progress.setValue(100 * i / databases.length);
        }
    }
}
