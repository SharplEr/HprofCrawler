package org.sharpler.hrofcrawler.dbs;

import java.io.IOException;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;

import org.iq80.leveldb.DB;
import org.iq80.leveldb.DBIterator;
import org.sharpler.hrofcrawler.Utils;
import org.sharpler.hrofcrawler.entries.InstanceEntry;

public final class InstancesDb implements AutoCloseable {
    private final DB db;
    private final BatchWriter writer;

    public InstancesDb(DB db) {
        this.db = db;
        writer = new BatchWriter(db::createWriteBatch, db::write, 1000);
    }

    public void put(long classId, long objectId, InstanceEntry entry) {
        writer.add(
                Utils.serializeTwoLong(classId, objectId),
                Utils.serialize(entry)
        );
    }

    public Optional<InstanceEntry> find(long classId, long objectId) {
        return Optional.ofNullable(db.get(Utils.serializeTwoLong(classId, objectId)))
                .map(InstancesDb::deserialize);
    }

    private static InstanceEntry deserialize(byte[] blob) {
        return Utils.deserialize(blob, InstanceEntry.class);
    }

    public void scan(long classId, Predicate<? super InstanceEntry> consumer) {
        try (DBIterator iterator = db.iterator()) {
            iterator.seek(Utils.serializeLong(classId));
            while (iterator.hasNext()) {
                Map.Entry<byte[], byte[]> entry = iterator.next();
                if (Utils.deserializeLong(entry.getKey()) != classId) {
                    break;
                }

                if (consumer.test(Utils.deserialize(entry.getValue(), InstanceEntry.class))) {
                    break;
                }
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void compact() {
        writer.flush();
        db.compactRange(Utils.serializeTwoLong(0L, 0L), Utils.serializeTwoLong(-1L, -1L));
    }

    @Override
    public void close() throws Exception {
        db.close();
    }
}
