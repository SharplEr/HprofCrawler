package org.sharpler.hprofcrawler.inspection;

import org.sharpler.hprofcrawler.api.ClassFilter;
import org.sharpler.hprofcrawler.api.Progress;
import org.sharpler.hprofcrawler.backend.Backend;
import org.sharpler.hprofcrawler.parser.Type;

import java.util.EnumSet;

public enum BuildInInspection implements Inspection {
    CONSTANT_FIELDS(
            new SimpleObjectInspection<>(
                    new ClassFilter.Builder()
                            .addIdFilter(x -> true)
                            .addClassFilter(x -> x.getCount() >= 1000 && !x.getFields().isEmpty())
                            .build(),
                    FindClassWithConstantField::new
            )
    ),
    WIDE_RANGE(
            new SimplePrimInspection<>(
                    EnumSet.of(Type.LONG, Type.INT, Type.SHORT),
                    FindPrimArrayWithTooWideRange::new
            )
    ),
    SPARSE_ARRAYS(
            new SimpleObjectArrayInspection<>(
                    ClassFilter.matchAll(),
                    FindSparseArrays::new
            )
    ),
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
