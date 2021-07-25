package org.sharpler.hprofcrawler.api;

import org.sharpler.hprofcrawler.views.ClassView;

import javax.annotation.Nullable;
import java.util.function.LongPredicate;
import java.util.function.Predicate;

// TODO: add included ids
public final class ClassFilter {
    private static final ClassFilter MATCH_ALL = new ClassFilter(x -> true, x -> true);
    private static final ClassFilter MATCH_NONE = new ClassFilter(x -> false, x -> false);
    private final LongPredicate idFilter;
    private final Predicate<ClassView> classFilter;

    private ClassFilter(LongPredicate idFilter, Predicate<ClassView> classFilter) {
        this.idFilter = idFilter;
        this.classFilter = classFilter;
    }

    public static ClassFilter matchAll() {
        return MATCH_ALL;
    }

    public static ClassFilter matchNone() {
        return MATCH_NONE;
    }

    public boolean filterId(long classId) {
        return idFilter.test(classId);
    }

    public boolean filterClass(ClassView classView) {
        return classFilter.test(classView);
    }

    public static final class Builder {
        @Nullable
        private LongPredicate idFilter = null;
        @Nullable
        private Predicate<ClassView> classFilter = null;

        public Builder() {
        }

        public Builder addIdFilter(LongPredicate filter) {
            if (idFilter == null) {
                idFilter = filter;
            } else {
                idFilter = idFilter.and(filter);
            }

            return this;
        }

        public Builder addClassFilter(Predicate<ClassView> filter) {
            if (classFilter == null) {
                classFilter = filter;
            } else {
                classFilter = classFilter.and(filter);
            }

            return this;
        }

        public ClassFilter build() {
            if (idFilter == null || classFilter == null) {
                throw new IllegalStateException();
            }

            return new ClassFilter(idFilter, classFilter);
        }
    }
}
