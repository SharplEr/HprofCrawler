package org.sharpler.hprofcrawler.backend;

import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

import org.sharpler.hprofcrawler.api.ClassFilter;
import org.sharpler.hprofcrawler.parser.PrimArray;
import org.sharpler.hprofcrawler.parser.Type;
import org.sharpler.hprofcrawler.views.ClassView;
import org.sharpler.hprofcrawler.views.InstanceView;
import org.sharpler.hprofcrawler.views.ObjectArrayView;

import javax.annotation.Nullable;

public interface Storage {
    Optional<InstanceView> lookupObject(long objectId);

    Optional<InstanceView> lookupObject(long classId, long objectId);

    void scanInstance(long classId, Predicate<? super InstanceView> consumer);

    void scanInstance(ClassView classView, Predicate<? super InstanceView> consumer);

    void scanPrimArray(Type type, Predicate<? super PrimArray> consumer);

    void scanObjectArray(long elementsClassId, Predicate<? super ObjectArrayView> consumer);

    void scanObjectArray(ClassView classView, Predicate<? super ObjectArrayView> consumer);

    @Nullable String resolveName(long id);

    List<ClassView> findClasses(ClassFilter filter);
}
