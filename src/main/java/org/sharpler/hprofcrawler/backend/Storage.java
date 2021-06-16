package org.sharpler.hprofcrawler.backend;

import java.util.Optional;
import java.util.function.Predicate;

import org.sharpler.hprofcrawler.parser.PrimArray;
import org.sharpler.hprofcrawler.parser.Type;
import org.sharpler.hprofcrawler.views.InstanceView;
import org.sharpler.hprofcrawler.views.ObjectArrayView;

public interface Storage {
    Optional<InstanceView> lookupObject(long objectId);

    Optional<InstanceView> lookupObject(long classId, long objectId);

    void scanClass(long classId, Predicate<? super InstanceView> consumer);

    void scanPrimArray(Type type, Predicate<? super PrimArray> consumer);

    void scanObjectArray(long elementsClassId, Predicate<? super ObjectArrayView> consumer);

    default void scanIntArray(Predicate<? super int[]> consumer) {
        scanPrimArray(Type.INT, x -> consumer.test((int[]) x.getArrayRaw()));
    }
}
