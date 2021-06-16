package org.sharpler.hprofcrawler.views;

import org.sharpler.hprofcrawler.parser.ObjectArray;

public final class ObjectArrayView {
    private final ObjectArray array;
    private final ClassView elementsClass;

    public ObjectArrayView(ObjectArray array, ClassView elementsClass) {
        this.array = array;
        this.elementsClass = elementsClass;
    }

    public ObjectArray getArray() {
        return array;
    }

    public ClassView getElementsClass() {
        return elementsClass;
    }
}
