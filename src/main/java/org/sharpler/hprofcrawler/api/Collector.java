package org.sharpler.hprofcrawler.api;

import java.util.function.LongFunction;

public interface Collector<TKey, TValue, TResult> {
    // InstanceCollector: ClassView -> InstanceView
    // ObjectArrayCollector: ClassView -> ObjectArrayView
    // PrimArrayCollector: Type -> PrimArray
    InstanceConsumer<TValue> getConsumer(TKey clazz);

    TResult buildResult(LongFunction<String> nameResolver);
}
