package org.sharpler.hrofcrawler.api;

import org.sharpler.hrofcrawler.views.ClassView;
import org.sharpler.hrofcrawler.views.ObjectArrayView;

import java.util.Collection;
import java.util.function.Predicate;
import java.util.stream.Stream;

public interface ObjectArrayScanOperation<T> {
    Stream<ClassView> classFilter(Collection<ClassView> classes);

    Predicate<ObjectArrayView> getConsumer(ClassView clazz);

    T buildResult();
}
