package org.sharpler.hrofcrawler.backend;

import java.util.Optional;
import java.util.function.Predicate;

import it.unimi.dsi.fastutil.longs.Long2LongOpenHashMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import org.sharpler.hrofcrawler.parser.PrimArray;
import org.sharpler.hrofcrawler.parser.Type;
import org.sharpler.hrofcrawler.views.InstanceView;
import org.sharpler.hrofcrawler.views.ObjectArrayView;

public final class HeapStorage implements Storage {

    private final Long2LongOpenHashMap object2Class;
    private final Long2ObjectOpenHashMap<Long2ObjectOpenHashMap<InstanceView>> instances;

    public HeapStorage(Long2LongOpenHashMap object2Class,
                       Long2ObjectOpenHashMap<Long2ObjectOpenHashMap<InstanceView>> instances)
    {
        this.object2Class = object2Class;
        this.instances = instances;
    }


    @Override
    public Optional<InstanceView> lookupObject(long objectId) {

        return Optional.ofNullable(instances.get(object2Class.get(objectId)))
                .map(x -> x.get(objectId));
    }

    @Override
    public Optional<InstanceView> lookupObject(long classId, long objectId) {
        return Optional.ofNullable(instances.get(classId)).map(x -> x.get(objectId));
    }

    @Override
    public void scanClass(long classId, Predicate<? super InstanceView> consumer) {
        Long2ObjectOpenHashMap<InstanceView> slice = instances.get(classId);
        if (slice != null) {
            for (InstanceView instanceView : slice.values()) {
                if (consumer.test(instanceView)) {
                    break;
                }
            }
        }
    }

    @Override
    public void scanPrimArray(Type type, Predicate<? super PrimArray> consumer) {

    }

    @Override
    public void scanObjectArray(long elementsClassId, Predicate<? super ObjectArrayView> consumer) {

    }
}