package org.sharpler.hprofcrawler.dbs;

import it.unimi.dsi.fastutil.objects.Object2LongOpenHashMap;
import org.iq80.leveldb.DB;
import org.iq80.leveldb.DBIterator;
import org.iq80.leveldb.ReadOptions;
import org.sharpler.hprofcrawler.Utils;
import org.sharpler.hprofcrawler.parser.PrimArray;
import org.sharpler.hprofcrawler.parser.Type;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;

/**
 * ({@link Type}, objectId) -> {@link PrimArray}.
 */
public class PrimArraysDb implements Database {
    private final DB db;
    private final BatchWriter writer;

    public PrimArraysDb(DB db) {
        this.db = db;
        writer = new BatchWriter(db::createWriteBatch, db::write);
    }

    public final void put(PrimArray primArray) {
        writer.add(
                Utils.serializeTwoLong(primArray.getType().ordinal(), primArray.getObjectId()),
                primArray.serialize()
        );
    }

    public Optional<PrimArray> find(Type type, long objectId) {
        return Optional.ofNullable(db.get(Utils.serializeTwoLong(type.ordinal(), objectId)))
                .map(PrimArray::deserialize);
    }

    public final void scan(Type type, Predicate<? super PrimArray> consumer) {
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
            throw new UncheckedIOException(e);
        }
    }

    // TODO: serialize that information into small file
    public final Object2LongOpenHashMap<Type> reloadIndex() {
        var result = new Object2LongOpenHashMap<Type>(Type.VALUES.size());
        try (DBIterator iterator = db.iterator(new ReadOptions().fillCache(false))) {
            iterator.seekToFirst();
            while (iterator.hasNext()) {
                result.addTo(Type.VALUES.get(Math.toIntExact(Utils.deserializeLong(iterator.next().getKey()))), 1);
            }

            return result;
        } catch (IOException e) {
            throw new UncheckedIOException(e);
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
