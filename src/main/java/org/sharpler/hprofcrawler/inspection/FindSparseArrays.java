package org.sharpler.hprofcrawler.inspection;

import it.unimi.dsi.fastutil.longs.Long2LongOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2LongOpenHashMap;
import org.sharpler.hprofcrawler.api.ObjectArrayScanOperation;
import org.sharpler.hprofcrawler.dbs.NamesDb;
import org.sharpler.hprofcrawler.views.ClassView;
import org.sharpler.hprofcrawler.views.ObjectArrayView;

import java.util.Collection;
import java.util.function.Predicate;
import java.util.stream.Stream;

public final class FindSparseArrays implements ObjectArrayScanOperation<Long2LongOpenHashMap> {
    private final Long2LongOpenHashMap stats = new Long2LongOpenHashMap();

    @Override
    public Stream<ClassView> classFilter(Collection<ClassView> classes) {
        // TODO: restore Void filtering
        return classes.stream();
    }

    @Override
    public Predicate<ObjectArrayView> getConsumer(ClassView clazz) {
        return x -> {
            for (long id : x.getArray().getValues()) {
                if (id == 0) {
                    stats.addTo(clazz.getName(), 1L);
                }
            }

            return false;
        };
    }

    @Override
    public Long2LongOpenHashMap buildResult() {
        return stats;
    }
}

