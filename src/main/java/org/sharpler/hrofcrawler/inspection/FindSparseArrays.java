package org.sharpler.hrofcrawler.inspection;

import it.unimi.dsi.fastutil.objects.Object2LongOpenHashMap;
import org.sharpler.hrofcrawler.api.ObjectArrayScanOperation;
import org.sharpler.hrofcrawler.views.ClassView;
import org.sharpler.hrofcrawler.views.ObjectArrayView;

import java.util.Collection;
import java.util.function.Predicate;
import java.util.stream.Stream;

public final class FindSparseArrays implements ObjectArrayScanOperation<Object2LongOpenHashMap<String>> {
    private final Object2LongOpenHashMap<String> stats = new Object2LongOpenHashMap<>();

    @Override
    public Stream<ClassView> classFilter(Collection<ClassView> classes) {
        return classes.stream()
                .filter(x -> !x.getName().equals(Void.class.getName()));
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
    public Object2LongOpenHashMap<String> buildResult() {
        return stats;
    }
}

