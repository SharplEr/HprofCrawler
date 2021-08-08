package org.sharpler.hprofcrawler.inspection;

import it.unimi.dsi.fastutil.longs.Long2LongOpenHashMap;
import org.sharpler.hprofcrawler.api.Collector;
import org.sharpler.hprofcrawler.api.InstanceConsumer;
import org.sharpler.hprofcrawler.views.ClassView;
import org.sharpler.hprofcrawler.views.ObjectArrayView;

import java.util.List;
import java.util.Objects;
import java.util.function.LongFunction;
import java.util.stream.Collectors;

public final class FindSparseArrays implements Collector<ClassView, ObjectArrayView, List<FindSparseArrays.Stat>> {
    private final Long2LongOpenHashMap stats = new Long2LongOpenHashMap();

    @Override
    public InstanceConsumer<ObjectArrayView> getConsumer(ClassView key) {
        return x -> {
            int zeroCount = 0;
            for (long id : x.getArray().getValues()) {
                if (id == 0) {
                    zeroCount++;
                }
            }
            if (zeroCount > x.getArray().getValues().length / 2) {
                stats.addTo(key.getName(), 1L);
            }

            return false;
        };
    }

    @Override
    public List<FindSparseArrays.Stat> buildResult(LongFunction<String> nameResolver) {
        return stats.long2LongEntrySet().stream()
                .map(e -> new Stat(Objects.requireNonNull(nameResolver.apply(e.getLongKey())), e.getLongValue()))
                .collect(Collectors.toList());
    }

    @SuppressWarnings("unused")
    public static final class Stat {
        private final String name;
        private final long count;

        private Stat(String name, long count) {
            this.name = name;
            this.count = count;
        }
    }
}

