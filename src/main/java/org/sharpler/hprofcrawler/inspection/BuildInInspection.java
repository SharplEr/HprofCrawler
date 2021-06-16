package org.sharpler.hprofcrawler.inspection;

import org.sharpler.hprofcrawler.api.Progress;
import org.sharpler.hprofcrawler.backend.Backend;

public enum BuildInInspection implements Inspection {
    CONSTANT_FIELDS(new SimpleObjectInspection<>(() -> new FindClassWithConstantField(1000))),
    WIDE_RANGE(new SimplePrimInspection<>(FindPrimArrayWithTooWideRange::new)),
    SPARSE_ARRAYS(new SimpleObjectArrayInspection<>(FindSparseArrays::new)),
    ;

    private final Inspection origin;

    BuildInInspection(Inspection origin) {
        this.origin = origin;
    }

    @Override
    public String run(Backend backend, Progress progress) {
        return origin.run(backend, progress);
    }
}
