package org.sharpler.hprofcrawler.inspection;

import org.sharpler.hprofcrawler.Utils;
import org.sharpler.hprofcrawler.api.ClassFilter;
import org.sharpler.hprofcrawler.api.Collector;
import org.sharpler.hprofcrawler.api.Progress;
import org.sharpler.hprofcrawler.backend.Backend;
import org.sharpler.hprofcrawler.views.ClassView;
import org.sharpler.hprofcrawler.views.ObjectArrayView;

import java.util.function.Supplier;

public final class SimpleObjectArrayInspection<T> implements Inspection {
    private final ClassFilter filter;
    private final Supplier<? extends Collector<ClassView, ObjectArrayView, ? extends T>> operationGenerator;

    public SimpleObjectArrayInspection(
            ClassFilter filter,
            Supplier<? extends Collector<ClassView, ObjectArrayView, ? extends T>> operationGenerator
    ) {
        this.filter = filter;
        this.operationGenerator = operationGenerator;
    }

    @Override
    public String run(Backend backend, Progress progress) {
        return Utils.toPrettyString((backend.scanObjectArray(filter, operationGenerator.get(), progress)));
    }
}
