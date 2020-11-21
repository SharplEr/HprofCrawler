package org.sharpler.hrofcrawler.backend;

import java.util.Optional;
import java.util.function.Predicate;

import org.sharpler.hrofcrawler.parser.PrimArray;
import org.sharpler.hrofcrawler.parser.Type;
import org.sharpler.hrofcrawler.views.InstanceView;
import org.sharpler.hrofcrawler.views.ObjectArrayView;

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
