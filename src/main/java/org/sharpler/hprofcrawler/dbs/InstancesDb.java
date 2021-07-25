package org.sharpler.hprofcrawler.dbs;

import org.iq80.leveldb.DB;
import org.iq80.leveldb.DBIterator;
import org.sharpler.hprofcrawler.Utils;
import org.sharpler.hprofcrawler.entries.InstanceEntry;

import java.io.IOException;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;

/**
 * Map: (classId, objectId) -> {@link InstanceEntry}.
 */
public final class InstancesDb implements Database {
    private final DB db;
    private final BatchWriter writer;

    public InstancesDb(DB db) {
        this.db = db;
        writer = new BatchWriter(db::createWriteBatch, db::write, 10000);
    }

    public void put(long classId, long objectId, InstanceEntry entry) {
        writer.add(
                Utils.serializeTwoLong(classId, objectId),
                entry.serialize()
        );
    }

    public Optional<InstanceEntry> find(long classId, long objectId) {
        return Optional.ofNullable(db.get(Utils.serializeTwoLong(classId, objectId)))
                .map(InstanceEntry::deserialize);
    }

    public void scan(long classId, Predicate<? super InstanceEntry> consumer) {
        try (DBIterator iterator = db.iterator()) {
            iterator.seek(Utils.serializeLong(classId));
            while (iterator.hasNext()) {
                Map.Entry<byte[], byte[]> entry = iterator.next();
                if (Utils.deserializeLong(entry.getKey()) != classId) {
                    break;
                }

                if (consumer.test(InstanceEntry.deserialize(entry.getValue()))) {
                    break;
                }
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void compact() {
        writer.flush();
        db.compactRange(Utils.serializeTwoLong(0L, 0L), Utils.serializeTwoLong(-1L, -1L));
    }

    @Override
    public void close() throws Exception {
        db.close();
    }
}
