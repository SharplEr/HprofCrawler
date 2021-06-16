package org.sharpler.hprofcrawler.api;

import org.sharpler.hprofcrawler.parser.PrimArray;
import org.sharpler.hprofcrawler.parser.Type;

import java.util.function.Predicate;
import java.util.stream.Stream;

public interface PrimArrayScanOperation<T> {
    Stream<Type> types();

    Predicate<PrimArray> getConsumer(Type type);

    T buildResult();
}
