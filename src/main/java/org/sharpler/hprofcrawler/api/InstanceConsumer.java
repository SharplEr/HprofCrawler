package org.sharpler.hprofcrawler.api;

import java.util.function.Predicate;

public interface InstanceConsumer<TInstance> extends Predicate<TInstance> {
    default void finish() {
    }
}
