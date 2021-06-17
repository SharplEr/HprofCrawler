package org.sharpler.hprofcrawler.inspection;

import org.sharpler.hprofcrawler.Utils;
import org.sharpler.hprofcrawler.api.Progress;
import org.sharpler.hprofcrawler.api.ScanOperation;
import org.sharpler.hprofcrawler.backend.Backend;

import java.util.function.Supplier;

public final class SimpleObjectInspection<T> implements Inspection {
    private final Supplier<? extends ScanOperation<T>> operationGenerator;

    public SimpleObjectInspection(Supplier<? extends ScanOperation<T>> operationGenerator) {
        this.operationGenerator = operationGenerator;
    }

    @Override
    public String run(Backend backend, Progress progress) {
        return Utils.toPrettyString(backend.scan(operationGenerator.get(), progress));
    }
}
