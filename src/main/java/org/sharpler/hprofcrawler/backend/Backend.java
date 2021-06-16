package org.sharpler.hprofcrawler.backend;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.sharpler.hprofcrawler.api.ObjectArrayScanOperation;
import org.sharpler.hprofcrawler.api.PrimArrayScanOperation;
import org.sharpler.hprofcrawler.api.Progress;
import org.sharpler.hprofcrawler.api.ScanOperation;
import org.sharpler.hprofcrawler.parser.Type;
import org.sharpler.hprofcrawler.views.ClassView;
import org.sharpler.hprofcrawler.views.InstanceView;

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
        List<ClassView> classes = operation.classFilter(index.getClasses().values())
                .filter(ClassView::isNotEmpty)
                .distinct()
                .collect(Collectors.toList());

        long total = classes.stream().mapToLong(ClassView::getCount).sum();
        long current = 0L;

        for (ClassView classView : classes) {
            storage.scanClass(classView.getId(), operation.getConsumer(classView));

            current += classView.getCount();
            progress.setValue(Math.toIntExact(current * 100L / total));
        }

        progress.done();
        return operation.buildResult();
    }

    public <T> T scanPrimArray(PrimArrayScanOperation<? extends T> operation, Progress progress) {
        List<Type> types = operation.types()
                .filter(x -> index.getPrimArrayCount(x) > 0)
                .distinct()
                .collect(Collectors.toList());

        long total = types.stream().mapToLong(index::getPrimArrayCount).sum();
        long current = 0L;

        for (Type type : types) {
            storage.scanPrimArray(type, operation.getConsumer(type));

            current += index.getPrimArrayCount(type);
            progress.setValue(Math.toIntExact(current * 100L / total));
        }
        progress.done();
        return operation.buildResult();
    }

    public <T> T scanObjectArray(ObjectArrayScanOperation<? extends T> operation, Progress progress) {
        List<ClassView> classes = operation.classFilter(index.getClasses().values())
                .filter(x -> index.getObjectArrayCount(x.getId()) > 0)
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
        return operation.buildResult();
    }
}
