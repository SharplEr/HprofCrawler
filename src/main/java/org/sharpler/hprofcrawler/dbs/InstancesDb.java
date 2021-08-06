package org.sharpler.hprofcrawler.dbs;

import org.iq80.leveldb.DB;
import org.iq80.leveldb.DBIterator;
import org.sharpler.hprofcrawler.Utils;
import org.sharpler.hprofcrawler.entries.InstanceEntry;
import org.sharpler.hprofcrawler.parser.Instance;
import org.sharpler.hprofcrawler.views.ClassView;
import org.sharpler.hprofcrawler.views.InstanceView;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.Map;
import java.util.function.Predicate;

/**
 * Map: (classId, objectId) -> {@link InstanceEntry}.
 */
public final class InstancesDb implements Database {
    private final DB db;
    private final BatchWriter writer;

    public InstancesDb(DB db) {
        this.db = db;
        writer = new BatchWriter(db::createWriteBatch, db::write);
    }

    public void put(Instance x) {
        writer.add(
                Utils.serializeTwoLong(x.classObjId, x.objId),
                x.serialize()
        );
    }

    public void scan(ClassView classView, Predicate<? super InstanceView> consumer) {
        try (DBIterator iterator = db.iterator()) {
            iterator.seek(Utils.serializeLong(classView.getId()));
            while (iterator.hasNext()) {
                Map.Entry<byte[], byte[]> entry = iterator.next();
                if (Utils.deserializeLong(entry.getKey()) != classView.getId()) {
                    break;
                }

                if (consumer.test(Instance.deserialize(entry.getValue(), classView))) {
                    break;
                }
            }

        } catch (IOException e) {
            throw new UncheckedIOException(e);
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
