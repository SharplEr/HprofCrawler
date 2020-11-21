package org.sharpler.hrofcrawler.inspection;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.sharpler.hrofcrawler.api.ScanOperation;
import org.sharpler.hrofcrawler.parser.Value;
import org.sharpler.hrofcrawler.views.ClassView;
import org.sharpler.hrofcrawler.views.InstanceView;

public final class FindClassWithConstantField implements ScanOperation<List<FindClassWithConstantField.Info>> {

    private final int minimalInstancesCount;

    private final HashMap<String, InfoBuilder> builders = new HashMap<>();

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
        private boolean[] isUnique = null;
        private List<Value> valuesBase = null;
        private final ClassView classView;

        public InfoBuilder(ClassView classView) {
            this.classView = classView;
            isUnique = new boolean[classView.getFields().size()];
            Arrays.fill(isUnique, true);
        }

        private void setup(InstanceView view) {
            valuesBase = view.getFields();

        }

        @Override
        public boolean test(InstanceView view) {
            if (valuesBase == null) {
                setup(view);
                return false;
            }
            boolean hasNoUnique = true;

            List<Value> values = view.getFields();

            for (int i = 0; i < valuesBase.size(); i++) {
                if (isUnique[i]) {
                    if (!valuesBase.get(i).equals(values.get(i))) {
                        isUnique[i] = false;
                    } else {
                        hasNoUnique = false;
                    }
                }
            }

            return hasNoUnique;
        }

        public Info build() {
            HashMap<String, Value> constants = new HashMap<>();

            for (int i = 0; i < valuesBase.size(); i++) {
                if (isUnique[i]) {
                    constants.put(classView.getFields().get(i).getName(), valuesBase.get(i));
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
        private final Map<String, Value> constants;

        public Info(ClassView classView, Map<String, Value> constants) {
            this.classView = classView;
            this.constants = constants;
        }

        public ClassView getClassView() {
            return classView;
        }

        public Map<String, Value> getConstants() {
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
