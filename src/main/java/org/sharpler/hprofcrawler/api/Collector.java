package org.sharpler.hprofcrawler.api;

import java.util.function.LongFunction;

/**
 * Collector help to run map-reduce like queries over the heap dump indices.
 * <p>
 * Work flow:
 * <p>
 * 1. Create {@link InstanceConsumer} once per every unique {@link TKey} in index.
 * 2. Consume all {@link TValue} with the same {@link TKey} by this {@link InstanceConsumer}.
 * 3. Press {@link InstanceConsumer#finish()} in the end.
 * 4. Call for {@link #buildResult(LongFunction)} for create final result of collecting.
 *
 * @param <TKey>    Type of key in heap dump index.
 *                  It could be {@link org.sharpler.hprofcrawler.views.ClassView} or
 *                  {@link org.sharpler.hprofcrawler.parser.Type}.
 * @param <TValue>  Type of values in heap dump index.
 *                  It could be {@link org.sharpler.hprofcrawler.views.InstanceView} or
 *                  {@link org.sharpler.hprofcrawler.parser.PrimArray} or
 *                  {@link org.sharpler.hprofcrawler.views.ObjectArrayView}.
 * @param <TResult> Type of final collection result.
 */
public interface Collector<TKey, TValue, TResult> {
    /**
     * Create new consumer for one specific key.
     *
     * @param key Key.
     * @return return Consumer.
     */
    InstanceConsumer<TValue> getConsumer(TKey key);

    /**
     * Build final collecting result.
     *
     * @param nameResolver Needs to resolve string {@code long} ids as {@link String}.
     * @return
     */
    TResult buildResult(LongFunction<String> nameResolver);
}
