package org.sharpler.hprofcrawler.backend;

import org.sharpler.hprofcrawler.api.ClassFilter;
import org.sharpler.hprofcrawler.api.Collector;
import org.sharpler.hprofcrawler.api.Progress;
import org.sharpler.hprofcrawler.parser.PrimArray;
import org.sharpler.hprofcrawler.parser.Type;
import org.sharpler.hprofcrawler.views.ClassView;
import org.sharpler.hprofcrawler.views.InstanceView;
import org.sharpler.hprofcrawler.views.ObjectArrayView;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public final class Backend {
    private final Storage storage;
    private final Index index;

    public Backend(Storage storage, Index index) {
        this.storage = storage;
        this.index = index;
    }

    public Index getIndex() {
        return index;
    }

    public Optional<InstanceView> lookup(long objectId) {
        return storage.lookupObject(objectId);
    }

    public Optional<InstanceView> lookup(long classId, long objectId) {
        return storage.lookupObject(classId, objectId);
    }

    public <T> T scanInstance(ClassFilter filter, Collector<ClassView, InstanceView, T> collector, Progress progress) {
        var classes = index.getClasses().values()
                .stream()
                .filter(ClassView::isNotEmpty)
                .filter(x -> filter.filterId(x.getId()) && filter.filterClass(x))
                .distinct()
                .collect(Collectors.toList());

        long total = classes.stream().mapToLong(ClassView::getCount).sum();
        long current = 0L;

        for (ClassView classView : classes) {
            var consumer = collector.getConsumer(classView);
            storage.scanClass(classView.getId(), consumer);
            consumer.finish();

            current += classView.getCount();
            progress.setValue(Math.toIntExact(current * 100L / total));
        }

        progress.done();
        return collector.buildResult(storage::resolveName);
    }

    public <T> T scanPrimArray(
            Set<Type> includedTypes,
            Collector<Type, PrimArray, ? extends T> operation,
            Progress progress
    ) {
        var types = Type.VALUES.stream()
                .filter(x -> index.getPrimArrayCount(x) > 0)
                .filter(includedTypes::contains)
                .collect(Collectors.toList());

        long total = types.stream().mapToLong(index::getPrimArrayCount).sum();
        long current = 0L;

        for (Type type : types) {
            var consumer = operation.getConsumer(type);
            storage.scanPrimArray(type, consumer);
            consumer.finish();

            current += index.getPrimArrayCount(type);
            progress.setValue(Math.toIntExact(current * 100L / total));
        }
        progress.done();
        return operation.buildResult(storage::resolveName);
    }

    public <T> T scanObjectArray(
            ClassFilter filter,
            Collector<ClassView, ObjectArrayView, ? extends T> operation,
            Progress progress
    ) {
        List<ClassView> classes = index.getClasses().values()
                .stream()
                .filter(x -> index.getObjectArrayCount(x.getId()) > 0)
                .filter(x -> filter.filterId(x.getId()) && filter.filterClass(x))
                .distinct()
                .collect(Collectors.toList());

        long total = classes.stream()
                .mapToLong(ClassView::getId)
                .map(index::getObjectArrayCount)
                .sum();
        long current = 0L;

        for (ClassView classView : classes) {
            storage.scanObjectArray(classView.getId(), operation.getConsumer(classView));

            current += index.getObjectArrayCount(classView.getId());
            progress.setValue(Math.toIntExact(current * 100L / total));
        }
        progress.done();
        return operation.buildResult(storage::resolveName);
    }
}
