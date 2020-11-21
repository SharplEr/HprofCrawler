package org.sharpler.hrofcrawler.backend;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.sharpler.hrofcrawler.api.ObjectArrayScanOperation;
import org.sharpler.hrofcrawler.api.PrimArrayScanOperation;
import org.sharpler.hrofcrawler.api.Progress;
import org.sharpler.hrofcrawler.api.ScanOperation;
import org.sharpler.hrofcrawler.parser.Type;
import org.sharpler.hrofcrawler.views.ClassView;
import org.sharpler.hrofcrawler.views.InstanceView;

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

    public <T> T scan(ScanOperation<? extends T> operation, Progress progress) {
        progress.reset();

        List<ClassView> classes = operation.classFilter(index.getClasses().values())
                .filter(ClassView::isNotEmpty)
                .distinct()
                .collect(Collectors.toList());

        long total = classes.stream().mapToLong(ClassView::getCount).sum();
        long current = 0L;

        for (int i = 0; i < classes.size(); i++) {
            ClassView classView = classes.get(i);
            storage.scanClass(classView.getId(), operation.getConsumer(classView));

            current += classView.getCount();
            progress.setValue(Math.toIntExact(current * 100L / total));
        }

        return operation.buildResult();
    }

    public <T> T scanPrimArray(PrimArrayScanOperation<? extends T> operation, Progress progress) {
        progress.reset();
        List<Type> types = operation.types()
                .filter(x -> index.getPrimArrayCount(x) > 0)
                .distinct()
                .collect(Collectors.toList());

        long total = types.stream().mapToLong(index::getPrimArrayCount).sum();
        long current = 0L;

        for (int i = 0; i < types.size(); i++) {
            Type type = types.get(i);
            storage.scanPrimArray(type, operation.getConsumer(type));

            current += index.getPrimArrayCount(type);
            progress.setValue(Math.toIntExact(current * 100L / total));
        }

        return operation.buildResult();
    }

    public <T> T scanObjectArray(ObjectArrayScanOperation<? extends T> operation, Progress progress) {
        progress.reset();

        List<ClassView> classes = operation.classFilter(index.getClasses().values())
                .filter(x -> index.getObjectArrayCount(x.getId()) > 0)
                .collect(Collectors.toList());

        long total = classes.stream()
                .mapToLong(ClassView::getId)
                .map(index::getObjectArrayCount)
                .sum();
        long current = 0L;

        for (int i = 0; i < classes.size(); i++) {
            ClassView classView = classes.get(i);
            storage.scanObjectArray(classView.getId(), operation.getConsumer(classView));

            current += index.getObjectArrayCount(classView.getId());
            progress.setValue(Math.toIntExact(current * 100L / total));
        }

        return operation.buildResult();
    }
}
