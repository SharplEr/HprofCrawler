package org.sharpler.hrofcrawler.backend;

import it.unimi.dsi.fastutil.longs.Long2LongOpenHashMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.objects.Object2LongOpenHashMap;
import org.sharpler.hrofcrawler.entries.FieldEntry;
import org.sharpler.hrofcrawler.parser.*;
import org.sharpler.hrofcrawler.views.ClassView;
import org.sharpler.hrofcrawler.views.InstanceView;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public final class InHeapBuilder extends DummyHandler implements BackendBuilder {
    private final Long2ObjectOpenHashMap<ClassView> classes = new Long2ObjectOpenHashMap<>();
    private final Long2ObjectOpenHashMap<String> names = new Long2ObjectOpenHashMap<>();

    private final Long2ObjectOpenHashMap<String> classesNames = new Long2ObjectOpenHashMap<>();

    private final Long2ObjectOpenHashMap<InstanceView> instances = new Long2ObjectOpenHashMap<>();

    private final Object2LongOpenHashMap<Type> primArrayCount = new Object2LongOpenHashMap<>();

    private final Long2LongOpenHashMap objectArrayCount = new Long2LongOpenHashMap();

    public void addName(long id, String name) {
        names.put(id, name);
    }

    @Override
    public void stringInUTF8(long id, String data) {
        names.put(id, data);
    }

    @Override
    public void loadClass(int classSerialNum, long classObjId, int stackTraceSerialNum, long classNameStringId) {
        classesNames.put(classObjId, Objects.requireNonNull(names.get(classNameStringId)));
    }


    @Override
    public void classDump(
            long classObjId,
            int stackTraceSerialNum,
            long superClassObjId,
            long classLoaderObjId,
            long signersObjId,
            long protectionDomainObjId,
            long reserved1,
            long reserved2,
            int instanceSize,
            Constant[] constants,
            Static[] statics,
            InstanceField[] instanceFields) {
        String className = Objects.requireNonNull(classesNames.get(classObjId));

        classes.put(
                classObjId,
                new ClassView(
                        className,
                        classObjId,
                        superClassObjId,
                        instanceSize,
                        Stream.of(instanceFields)
                                .map(x -> new FieldEntry(Objects.requireNonNull(names.get(x.fieldNameStringId)),
                                        x.type))
                                .collect(Collectors.toList())
                )
        );
    }

    @Override
    public void instanceDump(long objId, int stackTraceSerialNum, long classObjId, List<Value> instanceFieldValues) {
        ClassView classView = Objects.requireNonNull(classes.get(classObjId));
        classView.addCount();

        instances.put(
                objId,
                new InstanceView(objId, classView, instanceFieldValues)
        );
    }

    public Index buildIndex() {
        LongOpenHashSet unmarked = new LongOpenHashSet(
                classes.keySet()
        );

        while (!unmarked.isEmpty()) {
            LongOpenHashSet marked = new LongOpenHashSet();
            unmarked.forEach(
                    (long id) -> {
                        ClassView classView = classes.get(id);
                        long superClassId = classView.getSuperClassId();
                        if (superClassId == 0) {
                            marked.add(id);
                            // find Object class.
                        } else if (!unmarked.contains(superClassId) || marked.contains(superClassId)) {
                            ClassView superClassView = classes.get(superClassId);
                            classView.getFields().addAll(superClassView.getFields());
                            marked.add(id);
                        }
                    }
            );
            unmarked.removeAll(marked);

        }

        return new Index(classes, primArrayCount, objectArrayCount);
    }

    public HeapStorage buildStorage() {
        Long2LongOpenHashMap object2Class = new Long2LongOpenHashMap();
        Long2ObjectOpenHashMap<Long2ObjectOpenHashMap<InstanceView>> groupInstances = new Long2ObjectOpenHashMap<>();
        instances.long2ObjectEntrySet().fastForEach(
                e -> {
                    long classId = e.getValue().getClassView().getId();
                    object2Class.put(e.getLongKey(), classId);
                    groupInstances.computeIfAbsent(classId, k -> new Long2ObjectOpenHashMap<>())
                            .put(e.getLongKey(), e.getValue());
                }
        );

        return new HeapStorage(object2Class, groupInstances);
    }

    @Override
    public void primArrayDump(long objId, int stackTraceSerialNum, PrimArray array) {
        primArrayCount.addTo(array.getType(), 1L);
    }

    @Override
    public void objArrayDump(long objId, int stackTraceSerialNum, long elemClassObjId, long[] elems) {
        objectArrayCount.put(elemClassObjId, 1L);
    }

    @Override
    public Backend build() {
        return new Backend(
                buildStorage(),
                buildIndex()
        );
    }
}
