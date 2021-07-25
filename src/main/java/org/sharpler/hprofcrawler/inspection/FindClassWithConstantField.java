package org.sharpler.hprofcrawler.inspection;

import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectMaps;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import org.sharpler.hprofcrawler.api.ScanOperation;
import org.sharpler.hprofcrawler.parser.Value;
import org.sharpler.hprofcrawler.views.ClassView;
import org.sharpler.hprofcrawler.views.InstanceView;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public final class FindClassWithConstantField implements ScanOperation<List<FindClassWithConstantField.Info>> {

    private final int minimalInstancesCount;

    private final Long2ObjectOpenHashMap<InfoBuilder> builders = new Long2ObjectOpenHashMap<>();

    public FindClassWithConstantField(int minimalInstancesCount) {
        assert minimalInstancesCount > 1;
        this.minimalInstancesCount = minimalInstancesCount;
    }

    @Override
    public Stream<ClassView> classFilter(Collection<ClassView> classes) {
        return classes.stream()
                .filter(x -> x.getCount() >= minimalInstancesCount)
                .filter(x -> !x.getFields().isEmpty());
    }

    @Override
    public Predicate<InstanceView> getConsumer(ClassView clazz) {
        return builders.computeIfAbsent(clazz.getName(), k -> new InfoBuilder(clazz));
    }

    @Override
    public List<Info> buildResult() {
        return builders.values().stream()
                .map(InfoBuilder::build)
                .filter(x -> !x.getConstants().isEmpty())
                .collect(Collectors.toList());
    }

    private static class InfoBuilder implements Predicate<InstanceView> {
        private final boolean[] isUnique;
        @Nullable
        private List<Value> valuesBase = null;
        private final ClassView classView;

        public InfoBuilder(ClassView classView) {
            this.classView = classView;
            this.isUnique = new boolean[classView.getFields().size()];
            Arrays.fill(isUnique, true);
        }

        private void setup(InstanceView view) {
            valuesBase = view.getFields();
        }

        @Override
        public final boolean test(@Nonnull InstanceView view) {
            if (valuesBase == null) {
                setup(view);
                return false;
            }
            boolean hasNoUnique = true;

            List<Value> values = view.getFields();

            for (int i = 0; i < valuesBase.size(); i++) {
                if (isUnique[i]) {
                    if (valuesBase.get(i).equals(values.get(i))) {
                        hasNoUnique = false;
                    } else {
                        isUnique[i] = false;
                    }
                }
            }

            return hasNoUnique;
        }

        public final Info build() {
            if (valuesBase == null) {
                return new Info(classView, Long2ObjectMaps.emptyMap());
            }
            Long2ObjectOpenHashMap<Value> constants = new Long2ObjectOpenHashMap<>();

            for (int i = 0; i < valuesBase.size(); i++) {
                if (isUnique[i]) {
                    constants.put(classView.getFields().get(i).getFieldNameStringId(), valuesBase.get(i));
                }
            }

            return new Info(
                    classView,
                    constants
            );
        }
    }

    public static final class Info {
        private final ClassView classView;
        private final Long2ObjectMap<Value> constants;

        public Info(ClassView classView, Long2ObjectMap<Value> constants) {
            this.classView = classView;
            this.constants = constants;
        }

        public ClassView getClassView() {
            return classView;
        }

        public Long2ObjectMap<Value> getConstants() {
            return constants;
        }

        @Override
        public String toString() {
            return "Info{" +
                    "classView=" + classView +
                    ", constants=" + constants +
                    '}';
        }
    }
}
