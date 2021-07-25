package org.sharpler.hprofcrawler.inspection;

import org.sharpler.hprofcrawler.Utils;
import org.sharpler.hprofcrawler.api.ClassFilter;
import org.sharpler.hprofcrawler.api.Collector;
import org.sharpler.hprofcrawler.api.Progress;
import org.sharpler.hprofcrawler.backend.Backend;
import org.sharpler.hprofcrawler.views.ClassView;
import org.sharpler.hprofcrawler.views.InstanceView;

import java.util.function.Supplier;

public final class SimpleObjectInspection<T> implements Inspection {
    private final ClassFilter filter;
    private final Supplier<? extends Collector<ClassView, InstanceView, T>> operationGenerator;

    public SimpleObjectInspection(
            ClassFilter filter,
            Supplier<? extends Collector<ClassView, InstanceView, T>> operationGenerator
    ) {
        this.filter = filter;
        this.operationGenerator = operationGenerator;
    }

    @Override
    public String run(Backend backend, Progress progress) {
        return Utils.toPrettyString(backend.scanInstance(filter, operationGenerator.get(), progress));
    }
}
