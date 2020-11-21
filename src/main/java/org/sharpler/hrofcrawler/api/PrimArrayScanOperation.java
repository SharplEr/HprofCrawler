package org.sharpler.hrofcrawler.api;

import org.sharpler.hrofcrawler.parser.PrimArray;
import org.sharpler.hrofcrawler.parser.Type;

import java.util.function.Predicate;
import java.util.stream.Stream;

public interface PrimArrayScanOperation<T> {
    Stream<Type> types();

    Predicate<PrimArray> getConsumer(Type type);

    T buildResult();
}
