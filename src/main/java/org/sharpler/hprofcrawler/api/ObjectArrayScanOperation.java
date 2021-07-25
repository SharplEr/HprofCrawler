package org.sharpler.hprofcrawler.api;

import org.sharpler.hprofcrawler.dbs.NamesDb;
import org.sharpler.hprofcrawler.views.ClassView;
import org.sharpler.hprofcrawler.views.ObjectArrayView;

import java.util.Collection;
import java.util.function.Predicate;
import java.util.stream.Stream;

public interface ObjectArrayScanOperation<T> {
    Stream<ClassView> classFilter(Collection<ClassView> classes);

    Predicate<ObjectArrayView> getConsumer(ClassView clazz);

    T buildResult();
}
