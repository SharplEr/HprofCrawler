package org.sharpler.hprofcrawler.dbs;

import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import org.iq80.leveldb.DB;
import org.iq80.leveldb.DBIterator;
import org.iq80.leveldb.ReadOptions;
import org.sharpler.hprofcrawler.Utils;
import org.sharpler.hprofcrawler.api.ClassFilter;
import org.sharpler.hprofcrawler.views.ClassView;

import javax.annotation.Nullable;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.ArrayList;
import java.util.List;

public class ClassInfoDb implements Database {
    private final DB db;
    private final BatchWriter writer;

    public ClassInfoDb(DB db) {
        this.db = db;
        this.writer = new BatchWriter(db::createWriteBatch, db::write);
    }

    public void put(long classId, ClassView classView) {
        writer.add(
                Utils.serializeLong(classId),
                classView.serialize()
        );
    }

    public Long2ObjectOpenHashMap<ClassView> reloadIndex() {
        var result = new Long2ObjectOpenHashMap<ClassView>();
        try (DBIterator iterator = db.iterator(new ReadOptions().fillCache(false))) {
            iterator.seekToFirst();
            while (iterator.hasNext()) {
                var entry = iterator.next();
                result.put(
                        Utils.deserializeLong(entry.getKey()),
                        ClassView.deserialize(entry.getValue())
                );
            }

            return result;
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    public List<ClassView> find(ClassFilter filter) {
        var result = new ArrayList<ClassView>();
        try (DBIterator iterator = db.iterator(new ReadOptions().fillCache(false))) {
            iterator.seekToFirst();
            while (iterator.hasNext()) {
                var entry = iterator.next();
                var id = Utils.deserializeLong(entry.getKey());
                if (filter.filterId(id)) {
                    var classView = ClassView.deserialize(entry.getValue());
                    if (filter.filterClass(classView)) {
                        result.add(classView);
                    }
                }
            }

            return result;
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    @Nullable
    public ClassView find(long id) {
        var bytes = db.get(Utils.serializeLong(id));
        if (bytes != null) {
            return ClassView.deserialize(bytes);
        }

        return null;
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
