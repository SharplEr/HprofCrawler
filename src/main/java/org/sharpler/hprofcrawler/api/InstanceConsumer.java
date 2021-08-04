package org.sharpler.hprofcrawler.api;

import java.util.function.Predicate;

/**
 * Consumer of values {@link TInstance} with the same key.
 *
 * @param <TInstance> Type of objects in heap dump which collecting.
 */
public interface InstanceConsumer<TInstance> extends Predicate<TInstance> {
    /**
     * Consume an instance from heap dump.
     *
     * @param instance object from heap dump.
     * @return {@code} true if you finish to collecting values and {@code false} if you should keep going.
     */
    @Override
    boolean test(TInstance instance);

    /**
     * Finish collecting instance of one type.
     */
    default void finish() {
    }
}
