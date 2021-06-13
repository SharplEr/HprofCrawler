package org.sharpler.hrofcrawler.concurrent;

import javax.annotation.Nullable;

public final class Result<T> {
    private static final Result<Void> VOID_OK = new Result<>(null, null);

    @Nullable
    private final T value;
    @Nullable
    private final RuntimeException exception;

    private Result(@Nullable T value, @Nullable RuntimeException exception) {
        this.value = value;
        this.exception = exception;
    }

    public static Result<Void> ok() {
        return VOID_OK;
    }

    public static <T> Result<T> ok(@Nullable T value) {
        if (value == null) {
            // If `T` is not `Void`, it's an error.
            return (Result<T>) VOID_OK;
        }
        return new Result<>(value, null);
    }

    public static <T> Result<T> error(Exception exception) {
        return new Result<>(
                null,
                exception instanceof RuntimeException ? (RuntimeException) exception : new RuntimeException(exception)
        );
    }

    public T get() {
        if (exception != null) {    // check exception because value could be null as valid result;
            throw exception;
        }

        return value;   // VOID_OK case;
    }
}