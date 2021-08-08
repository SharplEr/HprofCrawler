package org.sharpler.hprofcrawler.inspection;

import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import org.sharpler.hprofcrawler.api.Collector;
import org.sharpler.hprofcrawler.api.InstanceConsumer;
import org.sharpler.hprofcrawler.parser.Value;
import org.sharpler.hprofcrawler.views.ClassView;
import org.sharpler.hprofcrawler.views.InstanceView;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.List;
import java.util.function.LongFunction;
import java.util.stream.Collectors;

public final class FindClassWithConstantField implements
        Collector<ClassView, InstanceView, List<FindClassWithConstantField.Stat>> {

    private final Long2ObjectOpenHashMap<InfoBuilder> builders = new Long2ObjectOpenHashMap<>();

    public FindClassWithConstantField() {
    }

    @Override
    public InstanceConsumer<InstanceView> getConsumer(ClassView key) {
        return builders.computeIfAbsent(key.getName(), k -> new InfoBuilder(key));
    }

    @Override
    public List<Stat> buildResult(LongFunction<String> nameResolver) {
        return builders.values().stream()
                .filter(x -> x.valuesBase != null)
                .map(x -> x.build(nameResolver))
                .collect(Collectors.toList());
    }

    private static class InfoBuilder implements InstanceConsumer<InstanceView> {
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
        public final boolean test(@Nonnull InstanceView instance) {
            if (valuesBase == null) {
                setup(instance);
                return false;
            }
            boolean hasNoUnique = true;

            List<Value> values = instance.getFields();

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

        public final Stat build(LongFunction<String> nameResolver) {
            if (valuesBase == null) {
                throw new IllegalStateException();
            }
            var constants = new Long2ObjectOpenHashMap<Value>();

            for (int i = 0; i < valuesBase.size(); i++) {
                if (isUnique[i]) {
                    constants.put(classView.getFields().get(i).getFieldNameStringId(), valuesBase.get(i));
                }
            }

            return new Stat(
                    nameResolver.apply(classView.getName()),
                    constants.long2ObjectEntrySet().stream()
                            .map(e -> new FieldInfo(nameResolver.apply(e.getLongKey()), e.getValue()))
                            .collect(Collectors.toList())
            );
        }
    }

    @SuppressWarnings("unused")
    static final class Stat {
        private final String name;
        private final List<FieldInfo> constants;

        public Stat(String name, List<FieldInfo> constants) {
            this.name = name;
            this.constants = constants;
        }
    }

    @SuppressWarnings("unused")
    public static final class FieldInfo {
        private final String fieldName;
        private final Value value;

        public FieldInfo(String fieldName, Value value) {
            this.fieldName = fieldName;
            this.value = value;
        }
    }
}
