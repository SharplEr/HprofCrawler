package org.sharpler.hprofcrawler.dbs;

import it.unimi.dsi.fastutil.longs.Long2LongOpenHashMap;
import org.iq80.leveldb.DB;
import org.iq80.leveldb.DBIterator;
import org.iq80.leveldb.ReadOptions;
import org.sharpler.hprofcrawler.Utils;
import org.sharpler.hprofcrawler.parser.ObjectArray;
import org.sharpler.hprofcrawler.parser.PrimArray;
import org.sharpler.hprofcrawler.parser.Type;

import java.io.IOException;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;

/**
 * Map: (classId, objectId) -> {@link PrimArray} where type == {@link Type#OBJ}.
 */
public class ObjectArraysDb implements Database {
    private final DB db;
    private final BatchWriter writer;

    public ObjectArraysDb(DB db) {
        this.db = db;
        writer = new BatchWriter(db::createWriteBatch, db::write, 10000);
    }

    public final void put(ObjectArray array) {
        writer.add(
                Utils.serializeTwoLong(array.getElementsClassId(), array.getObjectId()),
                array.serialize()
        );
    }

    public Optional<PrimArray> find(long elementsClassId, long objectId) {
        return Optional.ofNullable(db.get(Utils.serializeTwoLong(elementsClassId, objectId)))
                .map(PrimArray::deserialize);
    }

    public final void scan(long elementsClassId, Predicate<? super ObjectArray> consumer) {
        try (DBIterator iterator = db.iterator()) {
            iterator.seek(Utils.serializeLong(elementsClassId));
            while (iterator.hasNext()) {
                Map.Entry<byte[], byte[]> entry = iterator.next();
                if (Utils.deserializeLong(entry.getKey()) != elementsClassId) {
                    break;
                }

                if (consumer.test(ObjectArray.deserialize(entry.getValue()))) {
                    break;
                }
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public final Long2LongOpenHashMap reloadIndex() {
        var result = new Long2LongOpenHashMap();
        try (DBIterator iterator = db.iterator(new ReadOptions().fillCache(false))) {
            iterator.seekToFirst();
            while (iterator.hasNext()) {
                result.addTo(Utils.deserializeLong(iterator.next().getKey()), 1);
            }

            return result;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public final void compact() {
        writer.flush();
        db.compactRange(Utils.serializeTwoLong(0L, 0L), Utils.serializeTwoLong(-1L, -1L));
    }

    @Override
    public final void close() throws Exception {
        db.close();
    }
}

