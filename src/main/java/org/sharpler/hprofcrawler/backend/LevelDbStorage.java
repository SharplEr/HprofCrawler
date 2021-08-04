package org.sharpler.hprofcrawler.backend;

import org.sharpler.hprofcrawler.Utils;
import org.sharpler.hprofcrawler.api.ClassFilter;
import org.sharpler.hprofcrawler.dbs.ClassInfoDb;
import org.sharpler.hprofcrawler.dbs.InstancesDb;
import org.sharpler.hprofcrawler.dbs.NamesDb;
import org.sharpler.hprofcrawler.dbs.Object2ClassDb;
import org.sharpler.hprofcrawler.dbs.ObjectArraysDb;
import org.sharpler.hprofcrawler.dbs.PrimArraysDb;
import org.sharpler.hprofcrawler.parser.PrimArray;
import org.sharpler.hprofcrawler.parser.Type;
import org.sharpler.hprofcrawler.views.ClassView;
import org.sharpler.hprofcrawler.views.InstanceView;
import org.sharpler.hprofcrawler.views.ObjectArrayView;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;

public final class LevelDbStorage implements Storage, AutoCloseable {
    private final Index index;
    private final Object2ClassDb object2Class;
    private final InstancesDb instances;

    private final PrimArraysDb primArraysDb;

    private final ObjectArraysDb objectArraysDb;
    private final NamesDb namesDb;

    private final ClassInfoDb classes;

    public LevelDbStorage(
            Index index,
            Object2ClassDb object2Class,
            InstancesDb instances,
            PrimArraysDb primArraysDb,
            ObjectArraysDb objectArraysDb,
            NamesDb namesDb,
            ClassInfoDb classes) {
        this.index = index;
        this.object2Class = object2Class;
        this.instances = instances;
        this.primArraysDb = primArraysDb;
        this.objectArraysDb = objectArraysDb;
        this.namesDb = namesDb;
        this.classes = classes;
    }

    @Override
    public void scanInstance(ClassView classView, Predicate<? super InstanceView> consumer) {
        instances.scan(classView, consumer);
    }

    @Override
    public void scanPrimArray(Type type, Predicate<? super PrimArray> consumer) {
        primArraysDb.scan(type, consumer);
    }

    @Override
    public void scanObjectArray(long elementsClassId, Predicate<? super ObjectArrayView> consumer) {
        objectArraysDb.scan(
                elementsClassId,
                x -> consumer.test(new ObjectArrayView(x, Objects.requireNonNull(classes.find(x.getElementsClassId()))))
        );
    }

    @Override
    public void scanObjectArray(ClassView classView, Predicate<? super ObjectArrayView> consumer) {
        objectArraysDb.scan(
                classView.getId(),
                x -> consumer.test(new ObjectArrayView(x, classView))
        );
    }

    @Nullable
    @Override
    public String resolveName(long id) {
        return namesDb.find(id);
    }

    @Override
    public List<ClassView> findClasses(ClassFilter filter) {
        return classes.find(filter);
    }

    @Override
    public void close() {
        Utils.closeAll(
                object2Class,
                instances,
                primArraysDb,
                objectArraysDb
        );
    }
}
