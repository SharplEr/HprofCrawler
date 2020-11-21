package org.sharpler.hrofcrawler.views;

import org.sharpler.hrofcrawler.parser.ObjectArray;

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
