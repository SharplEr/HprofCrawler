package org.sharpler.hprofcrawler.dbs;

import it.unimi.dsi.fastutil.objects.Object2LongOpenHashMap;
import org.rocksdb.ColumnFamilyHandle;
import org.rocksdb.RocksDB;
import org.sharpler.hprofcrawler.Utils;
import org.sharpler.hprofcrawler.parser.PrimArray;
import org.sharpler.hprofcrawler.parser.Type;

import javax.annotation.Nullable;
import java.util.function.Predicate;

/**
 * ({@link Type}, objectId) -> {@link PrimArray}.
 */
public class PrimArraysDb extends Database {
    public PrimArraysDb(RocksDB db, ColumnFamilyHandle handle) {
        super(db, handle);
    }

    public final void put(PrimArray primArray) {
        put(
                Utils.serializeTwoLong(primArray.getType().ordinal(), primArray.getObjectId()),
                primArray.serialize()
        );
    }

    @Nullable
    public PrimArray find(Type type, long objectId) {
        return Utils.map(
                find(Utils.serializeTwoLong(type.ordinal(), objectId)),
                PrimArray::deserialize
        );
    }

    public final void scan(Type type, Predicate<? super PrimArray> consumer) {
        try (var iterator = iterator()) {
            for (iterator.seek(Utils.serializeLong(type.ordinal())); iterator.isValid(); iterator.next()) {
                if (Utils.deserializeLong(iterator.key()) != type.ordinal()) {
                    break;
                }

                if (consumer.test(PrimArray.deserialize(iterator.value()))) {
                    break;
                }
            }
        }
    }

    // TODO: serialize that information into small file
    public final Object2LongOpenHashMap<Type> reloadIndex() {
        var result = new Object2LongOpenHashMap<Type>(Type.VALUES.size());
        try (var iterator = iterator()) {
            for (iterator.seekToFirst(); iterator.isValid(); iterator.next()) {
                result.addTo(Type.VALUES.get(Math.toIntExact(Utils.deserializeLong(iterator.key()))), 1);
            }
            return result;
        }
    }
}
