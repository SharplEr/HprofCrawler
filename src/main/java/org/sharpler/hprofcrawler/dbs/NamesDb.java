package org.sharpler.hprofcrawler.dbs;

import org.iq80.leveldb.DB;
import org.sharpler.hprofcrawler.Utils;

import javax.annotation.Nullable;

/**
 * Map: stringId -> {@link String}.
 */
public final class NamesDb implements Database {
    private final DB db;
    private final BatchWriter writer;

    public NamesDb(DB db) {
        this.db = db;
        this.writer = new BatchWriter(db::createWriteBatch, db::write);
    }

    public void put(long id, String name) {
        writer.add(
                Utils.serializeLong(id),
                name.getBytes()
        );
    }

    @Nullable
    public String find(long id) {
        return Utils.map(db.get(Utils.serializeLong(id)), String::new);
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
