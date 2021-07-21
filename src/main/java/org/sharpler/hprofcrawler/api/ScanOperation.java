package org.sharpler.hprofcrawler.api;

import java.util.Collection;
import java.util.function.Predicate;
import java.util.stream.Stream;

import org.sharpler.hprofcrawler.views.ClassView;
import org.sharpler.hprofcrawler.views.InstanceView;

public interface ScanOperation<T> {
    Stream<ClassView> classFilter(Collection<ClassView> classes);

    Predicate<InstanceView> getConsumer(ClassView clazz);

    T buildResult();
}
