package org.sharpler.hprofcrawler.inspection;

import org.sharpler.hprofcrawler.Utils;
import org.sharpler.hprofcrawler.api.PrimArrayScanOperation;
import org.sharpler.hprofcrawler.api.Progress;
import org.sharpler.hprofcrawler.backend.Backend;

import java.util.function.Supplier;

public final class SimplePrimInspection<T> implements Inspection {
    private final Supplier<? extends PrimArrayScanOperation<? extends T>> operationGenerator;

    public SimplePrimInspection(Supplier<? extends PrimArrayScanOperation<? extends T>> operationGenerator) {
        this.operationGenerator = operationGenerator;
    }

    @Override
    public String run(Backend backend, Progress progress) {
        return Utils.toPrettyString(backend.scanPrimArray(operationGenerator.get(), progress));
    }
}
