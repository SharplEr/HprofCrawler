package org.sharpler.hprofcrawler.backend;

import it.unimi.dsi.fastutil.longs.Long2LongOpenHashMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.objects.Object2LongOpenHashMap;
import org.sharpler.hprofcrawler.dbs.ClassInfoDb;
import org.sharpler.hprofcrawler.dbs.InstancesDb;
import org.sharpler.hprofcrawler.dbs.NamesDb;
import org.sharpler.hprofcrawler.dbs.Object2ClassDb;
import org.sharpler.hprofcrawler.dbs.ObjectArraysDb;
import org.sharpler.hprofcrawler.dbs.PrimArraysDb;
import org.sharpler.hprofcrawler.parser.Constant;
import org.sharpler.hprofcrawler.parser.DummyHandler;
import org.sharpler.hprofcrawler.parser.Instance;
import org.sharpler.hprofcrawler.parser.InstanceField;
import org.sharpler.hprofcrawler.parser.ObjectArray;
import org.sharpler.hprofcrawler.parser.PrimArray;
import org.sharpler.hprofcrawler.parser.Static;
import org.sharpler.hprofcrawler.parser.Type;
import org.sharpler.hprofcrawler.views.ClassView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;

public final class RocksDbBuilder extends DummyHandler implements BackendBuilder {

    private final Long2ObjectOpenHashMap<ClassView> classes = new Long2ObjectOpenHashMap<>();

    private final Long2LongOpenHashMap classesNames = new Long2LongOpenHashMap();

    private final Long2LongOpenHashMap objectArrayCount = new Long2LongOpenHashMap();

    private final Object2ClassDb object2ClassDb;
    private final InstancesDb instancesDb;

    private final PrimArraysDb primArraysDb;

    private final ObjectArraysDb objectArraysDb;

    private final Object2LongOpenHashMap<Type> primArrayCount = new Object2LongOpenHashMap<>();

    private final NamesDb namesDb;

    private final ClassInfoDb classInfoDb;

    public RocksDbBuilder(
            Object2ClassDb object2ClassDb,
            InstancesDb instancesDb,
            PrimArraysDb primArraysDb,
            ObjectArraysDb objectArraysDb,
            NamesDb namesDb,
            ClassInfoDb classInfoDb
    ) {
        this.object2ClassDb = object2ClassDb;
        this.instancesDb = instancesDb;
        this.primArraysDb = primArraysDb;
        this.objectArraysDb = objectArraysDb;
        this.namesDb = namesDb;
        this.classInfoDb = classInfoDb;
    }

    @Override
    public void stringInUTF8(long id, String data) {
        namesDb.put(id, data);
    }

    @Override
    public void loadClass(int classSerialNum, long classObjId, int stackTraceSerialNum, long classNameStringId) {
        classesNames.put(classObjId, classNameStringId);
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
            InstanceField[] instanceFields
    ) {
        classes.put(
                classObjId,
                ClassView.create(
                        classesNames.get(classObjId),
                        classObjId,
                        superClassObjId,
                        instanceSize,
                        new ArrayList<>(Arrays.asList(instanceFields))
                )
        );
    }

    @Override
    public void instanceDump(Instance instance) {
        ClassView classView = Objects.requireNonNull(classes.get(instance.classObjId));
        classView.addCount();

        object2ClassDb.put(instance.objId, instance.classObjId);
        instancesDb.put(instance);
    }

    @Override
    public void objArrayDump(long objId, int stackTraceSerialNum, long elemClassObjId, long[] elems) {
        objectArrayCount.addTo(elemClassObjId, 1L);
        objectArraysDb.put(new ObjectArray(objId, elemClassObjId, elems));
    }

    @Override
    public void primArrayDump(long objId, int stackTraceSerialNum, PrimArray array) {
        primArrayCount.addTo(array.getType(), 1L);
        primArraysDb.put(array);
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

        classes.long2ObjectEntrySet().forEach(e -> classInfoDb.put(e.getLongKey(), e.getValue()));

        return new Index(primArrayCount, objectArrayCount);
    }


    public RocksDbStorage buildStorage() {
        return new RocksDbStorage(
                object2ClassDb,
                instancesDb,
                primArraysDb,
                objectArraysDb,
                namesDb,
                classInfoDb
        );
    }

    @Override
    public Backend build() {
        return new Backend(buildStorage(), buildIndex());
    }
}
