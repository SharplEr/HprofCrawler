package org.sharpler.hprofcrawler.backend;

import it.unimi.dsi.fastutil.longs.Long2LongOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2LongOpenHashMap;
import org.sharpler.hprofcrawler.dbs.ObjectArraysDb;
import org.sharpler.hprofcrawler.dbs.PrimArraysDb;
import org.sharpler.hprofcrawler.parser.Type;

public final class Index {

    private final Object2LongOpenHashMap<Type> primArrayCount;

    private final Long2LongOpenHashMap objectArrayCount;

    public Index(Object2LongOpenHashMap<Type> primArrayCount, Long2LongOpenHashMap objectArrayCount) {
        this.primArrayCount = primArrayCount;
        this.objectArrayCount = objectArrayCount;
    }

    public static Index reload(PrimArraysDb primArraysDb, ObjectArraysDb objectArraysDb) {
        return new Index(primArraysDb.reloadIndex(), objectArraysDb.reloadIndex());
    }

    public long getPrimArrayCount(Type type) {
        return primArrayCount.getLong(type);
    }

    public long getObjectArrayCount(long elementClassId) {
        return objectArrayCount.get(elementClassId);
    }
}
