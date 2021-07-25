package org.sharpler.hprofcrawler.inspection;

import org.sharpler.hprofcrawler.Utils;
import org.sharpler.hprofcrawler.api.Collector;
import org.sharpler.hprofcrawler.api.Progress;
import org.sharpler.hprofcrawler.backend.Backend;
import org.sharpler.hprofcrawler.parser.PrimArray;
import org.sharpler.hprofcrawler.parser.Type;

import java.util.Set;
import java.util.function.Supplier;

public final class SimplePrimInspection<T> implements Inspection {
    private final Set<Type> types;
    private final Supplier<? extends Collector<Type, PrimArray, ? extends T>> operationGenerator;

    public SimplePrimInspection(
            Set<Type> types,
            Supplier<? extends Collector<Type, PrimArray, ? extends T>> operationGenerator
    ) {
        this.types = types;
        this.operationGenerator = operationGenerator;
    }

    @Override
    public String run(Backend backend, Progress progress) {
        return Utils.toPrettyString(backend.scanPrimArray(types, operationGenerator.get(), progress));
    }
}
