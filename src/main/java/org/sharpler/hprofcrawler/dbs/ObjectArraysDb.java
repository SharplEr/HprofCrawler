package org.sharpler.hprofcrawler.dbs;

import it.unimi.dsi.fastutil.longs.Long2LongOpenHashMap;
import org.rocksdb.ColumnFamilyHandle;
import org.rocksdb.RocksDB;
import org.sharpler.hprofcrawler.Utils;
import org.sharpler.hprofcrawler.parser.ObjectArray;
import org.sharpler.hprofcrawler.parser.PrimArray;
import org.sharpler.hprofcrawler.parser.Type;

import javax.annotation.Nullable;
import java.util.function.Predicate;

/**
 * Map: (classId, objectId) -> {@link PrimArray} where type == {@link Type#OBJ}.
 */
public class ObjectArraysDb extends Database {

    public ObjectArraysDb(RocksDB db, ColumnFamilyHandle handle) {
        super(db, handle);
    }

    public final void put(ObjectArray array) {
        put(
                Utils.serializeTwoLong(array.getElementsClassId(), array.getObjectId()),
                array.serialize()
        );
    }

    @Nullable
    public PrimArray find(long elementsClassId, long objectId) {
        return Utils.map(find(Utils.serializeTwoLong(elementsClassId, objectId)), PrimArray::deserialize);
    }

    public final void scan(long elementsClassId, Predicate<? super ObjectArray> consumer) {
        try (var iterator = iterator()) {
            for (iterator.seek(Utils.serializeLong(elementsClassId)); iterator.isValid(); iterator.next()) {
                if (Utils.deserializeLong(iterator.key()) != elementsClassId) {
                    break;
                }

                if (consumer.test(ObjectArray.deserialize(iterator.value()))) {
                    break;
                }
            }
        }
    }

    public final Long2LongOpenHashMap reloadIndex() {
        var result = new Long2LongOpenHashMap();
        try (var iterator = iterator()) {
            for (iterator.seekToFirst(); iterator.isValid(); iterator.next()) {
                result.addTo(Utils.deserializeLong(iterator.key()), 1);
            }
            return result;
        }
    }
}

