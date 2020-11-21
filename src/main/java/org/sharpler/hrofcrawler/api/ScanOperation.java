package org.sharpler.hrofcrawler.api;

import java.util.Collection;
import java.util.function.Predicate;
import java.util.stream.Stream;

import org.sharpler.hrofcrawler.views.ClassView;
import org.sharpler.hrofcrawler.views.InstanceView;

public interface ScanOperation<T> {
    public Stream<ClassView> classFilter(Collection<ClassView> classes);

    Predicate<InstanceView> getConsumer(ClassView clazz);

    T buildResult();
}
