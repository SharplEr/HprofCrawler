package org.sharpler.hrofcrawler.concurrent;

import javax.annotation.Nullable;
import java.util.function.Supplier;

/**
 * Wraps {@link Supplier} and clean reference on it after first call.
 * <p>
 * It could help to avoid memory leaks.
 */
public final class Lazy<T> implements Runnable, Supplier<T> {
    private static final Supplier<?> EMPTY = () -> null;

    private static <T> Supplier<T> empty() {
        return (Supplier<T>) EMPTY;
    }

    private volatile Supplier<? extends T> supp;
    @Nullable
    private Result<T> val;

    public Lazy(Supplier<? extends T> supp) {
        this.supp = supp;
    }

    public T get() {
        if (supp != EMPTY) {
            synchronized (this) {
                if (val == null) {
                    try {
                        val = Result.ok(supp.get());
                    } catch (Exception e) {
                        val = Result.error(e);
                    }
                    supp = empty();
                }
            }
        }
        return val.get();
    }

    @Override
    public void run() {
        get();
    }
}
