package org.sharpler.hprofcrawler.backend;

import java.util.Optional;
import java.util.OptionalLong;
import java.util.function.Predicate;

import org.sharpler.hprofcrawler.Utils;
import org.sharpler.hprofcrawler.dbs.InstancesDb;
import org.sharpler.hprofcrawler.dbs.Object2ClassDb;
import org.sharpler.hprofcrawler.dbs.ObjectArraysDb;
import org.sharpler.hprofcrawler.dbs.PrimArraysDb;
import org.sharpler.hprofcrawler.parser.PrimArray;
import org.sharpler.hprofcrawler.parser.Type;
import org.sharpler.hprofcrawler.views.InstanceView;
import org.sharpler.hprofcrawler.views.ObjectArrayView;

public final class LevelDbStorage implements Storage, AutoCloseable {
    private final Index index;
    private final Object2ClassDb object2Class;
    private final InstancesDb instances;

    private final PrimArraysDb primArraysDb;

    private final ObjectArraysDb objectArraysDb;

    public LevelDbStorage(
            Index index,
            Object2ClassDb object2Class,
            InstancesDb instances, PrimArraysDb primArraysDb,
            ObjectArraysDb objectArraysDb)
    {
        this.index = index;
        this.object2Class = object2Class;
        this.instances = instances;
        this.primArraysDb = primArraysDb;
        this.objectArraysDb = objectArraysDb;
    }

    @Override
    public Optional<InstanceView> lookupObject(long objectId) {
        OptionalLong classId = object2Class.findClassId(objectId);

        if (classId.isEmpty()) {
            return Optional.empty();
        }

        return instances.find(classId.getAsLong(), objectId)
                .map(x -> InstanceView.of(x, index));
    }

    @Override
    public Optional<InstanceView> lookupObject(long classId, long objectId) {
        return instances.find(classId, objectId)
                .map(x -> InstanceView.of(x, index));
    }

    @Override
    public void scanClass(long classId, Predicate<? super InstanceView> consumer) {
        instances.scan(classId, x -> consumer.test(InstanceView.of(x, index)));
    }

    @Override
    public void scanPrimArray(Type type, Predicate<? super PrimArray> consumer) {
        primArraysDb.scan(type, consumer);
    }

    @Override
    public void scanObjectArray(long elementsClassId, Predicate<? super ObjectArrayView> consumer) {
        objectArraysDb.scan(
                elementsClassId,
                x -> consumer.test(new ObjectArrayView(x, index.findClassView(x.getElementsClassId())))
        );
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
