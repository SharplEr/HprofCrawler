package org.sharpler.hprofcrawler.backend;

import it.unimi.dsi.fastutil.longs.Long2LongOpenHashMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.objects.Object2LongOpenHashMap;
import org.sharpler.hprofcrawler.Utils;
import org.sharpler.hprofcrawler.api.Progress;
import org.sharpler.hprofcrawler.dbs.InstancesDb;
import org.sharpler.hprofcrawler.dbs.Object2ClassDb;
import org.sharpler.hprofcrawler.dbs.ObjectArraysDb;
import org.sharpler.hprofcrawler.dbs.PrimArraysDb;
import org.sharpler.hprofcrawler.entries.FieldEntry;
import org.sharpler.hprofcrawler.entries.InstanceEntry;
import org.sharpler.hprofcrawler.parser.Constant;
import org.sharpler.hprofcrawler.parser.DummyHandler;
import org.sharpler.hprofcrawler.parser.InstanceField;
import org.sharpler.hprofcrawler.parser.ObjectArray;
import org.sharpler.hprofcrawler.parser.PrimArray;
import org.sharpler.hprofcrawler.parser.Static;
import org.sharpler.hprofcrawler.parser.Type;
import org.sharpler.hprofcrawler.parser.Value;
import org.sharpler.hprofcrawler.views.ClassView;

import java.nio.file.Path;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public final class LevelDbBuilder extends DummyHandler implements BackendBuilder {

    private final Long2ObjectOpenHashMap<ClassView> classes = new Long2ObjectOpenHashMap<>();
    private final Long2ObjectOpenHashMap<String> names = new Long2ObjectOpenHashMap<>();

    private final Long2ObjectOpenHashMap<String> classesNames = new Long2ObjectOpenHashMap<>();

    private final Long2LongOpenHashMap objectArrayCount = new Long2LongOpenHashMap();

    private final Object2ClassDb object2ClassDb;
    private final InstancesDb instancesDb;

    private final PrimArraysDb primArraysDb;

    private final ObjectArraysDb objectArraysDb;

    private final Object2LongOpenHashMap<Type> primArrayCount = new Object2LongOpenHashMap<>();

    private LevelDbBuilder(
            Object2ClassDb object2ClassDb,
            InstancesDb instancesDb,
            PrimArraysDb primArraysDb,
            ObjectArraysDb objectArraysDb) {
        this.object2ClassDb = object2ClassDb;
        this.instancesDb = instancesDb;
        this.primArraysDb = primArraysDb;
        this.objectArraysDb = objectArraysDb;
    }

    public static LevelDbBuilder of(Path dir) {
        return Utils.resourceOwner(
                LevelDbBuilder::new,
                () -> new Object2ClassDb(Utils.openDb(dir.resolve("object2Class"))),
                () -> new InstancesDb(Utils.openDb(dir.resolve("instances"))),
                () -> new PrimArraysDb(Utils.openDb(dir.resolve("prim_arrays"))),
                () -> new ObjectArraysDb(Utils.openDb(dir.resolve("object_arrays")))
        );
    }

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
    public void instanceDump(long objId, int stackTraceSerialNum, long classObjId, List<Value> fields) {
        ClassView classView = Objects.requireNonNull(classes.get(classObjId));
        classView.addCount();

        object2ClassDb.put(objId, classObjId);

        instancesDb.put(classObjId, objId, new InstanceEntry(objId, classObjId, fields));
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

        return new Index(classes, primArrayCount, objectArrayCount);
    }

    public LevelDbStorage buildStorage(Index index, Progress progress) {
        object2ClassDb.compact();
        progress.setValue(25);
        instancesDb.compact();
        progress.setValue(50);
        primArraysDb.compact();
        progress.setValue(75);
        objectArraysDb.compact();
        progress.done();

        return new LevelDbStorage(
                index,
                object2ClassDb,
                instancesDb,
                primArraysDb,
                objectArraysDb
        );
    }

    @Override
    public Backend build(Progress progress) {
        Index index = buildIndex();

        return new Backend(
                buildStorage(index, progress),
                index
        );
    }
}
