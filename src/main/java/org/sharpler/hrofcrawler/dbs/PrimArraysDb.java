package org.sharpler.hrofcrawler.dbs;

import org.iq80.leveldb.DB;
import org.iq80.leveldb.DBIterator;
import org.sharpler.hrofcrawler.Utils;
import org.sharpler.hrofcrawler.parser.PrimArray;
import org.sharpler.hrofcrawler.parser.Type;

import java.io.IOException;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;

public class PrimArraysDb implements AutoCloseable {
    private final DB db;
    private final BatchWriter writer;

    public PrimArraysDb(DB db) {
        this.db = db;
        writer = new BatchWriter(db::createWriteBatch, db::write, 1000);
    }

    public void put(PrimArray primArray) {
        writer.add(
                Utils.serializeTwoLong(primArray.getType().ordinal(), primArray.getObjectId()),
                primArray.serialize()
        );
    }

    public Optional<PrimArray> find(Type type, long objectId) {
        return Optional.ofNullable(db.get(Utils.serializeTwoLong(type.ordinal(), objectId)))
                .map(PrimArray::deserialize);
    }

    public void scan(Type type, Predicate<? super PrimArray> consumer) {
        try (DBIterator iterator = db.iterator()) {
            iterator.seek(Utils.serializeLong(type.ordinal()));
            while (iterator.hasNext()) {
                Map.Entry<byte[], byte[]> entry = iterator.next();
                if (Utils.deserializeLong(entry.getKey()) != type.ordinal()) {
                    break;
                }

                if (consumer.test(PrimArray.deserialize(entry.getValue()))) {
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