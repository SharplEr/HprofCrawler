package org.sharpler.hprofcrawler.dbs;

import java.util.OptionalLong;

import org.iq80.leveldb.DB;
import org.sharpler.hprofcrawler.Utils;

/**
 * Map: objectId->classId.
 */
public final class Object2ClassDb implements Database {
    private final DB db;
    private final BatchWriter writer;

    public Object2ClassDb(DB db) {
        this.db = db;
        writer = new BatchWriter(db::createWriteBatch, db::write);
    }

    public void put(long objectId, long classId) {
        writer.add(Utils.serializeLong(objectId), Utils.serializeLong(classId));
    }

    public OptionalLong findClassId(long objectId) {
        byte[] classId = db.get(Utils.serializeLong(objectId));
        return classId == null ? OptionalLong.empty() : OptionalLong.of(Utils.deserializeLong(classId));
    }

    @Override
    public void compact() {
        writer.flush();
        db.compactRange(Utils.serializeLong(0L), Utils.serializeLong(-1L));
    }

    @Override
    public void close() throws Exception {
        db.close();
    }
}

