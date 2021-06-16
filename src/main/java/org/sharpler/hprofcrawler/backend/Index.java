package org.sharpler.hprofcrawler.backend;

import java.util.Objects;

import it.unimi.dsi.fastutil.longs.Long2LongOpenHashMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2LongOpenHashMap;
import org.sharpler.hprofcrawler.parser.Type;
import org.sharpler.hprofcrawler.views.ClassView;

public final class Index {
    private final Long2ObjectOpenHashMap<ClassView> classes;

    private final Object2LongOpenHashMap<Type> primArrayCount;

    private final Long2LongOpenHashMap objectArrayCount;

    public Index(
            Long2ObjectOpenHashMap<ClassView> classes,
            Object2LongOpenHashMap<Type> primArrayCount,
            Long2LongOpenHashMap objectArrayCount)
    {
        this.classes = classes;
        this.primArrayCount = primArrayCount;
        this.objectArrayCount = objectArrayCount;
    }

    public ClassView findClassView(long id) {
        return Objects.requireNonNull(classes.get(id));
    }

    public long getPrimArrayCount(Type type) {
        return primArrayCount.getLong(type);
    }

    public long getObjectArrayCount(long elementClassId) {
        return objectArrayCount.get(elementClassId);
    }

    public Long2ObjectOpenHashMap<ClassView> getClasses() {
        return classes;
    }

    public int classesCount() {
        return classes.size();
    }
}
