package org.sharpler.hrofcrawler;

import java.util.function.Consumer;

public final class Result<TOk, TError> {
    private final Object value;
    private final boolean isOk;

    private Result(Object value, boolean isOk) {
        this.value = value;
        this.isOk = isOk;
    }

    public static <TOk, TError> Result<TOk, TError> ok(TOk value) {
        return new Result<>(value, true);
    }

    public static <TOk, TError> Result<TOk, TError> error(TError value) {
        return new Result<>(value, false);
    }

    public TOk getOk() {
        if (!isOk) {
            throw new IllegalStateException("isOk == false");
        }
        return (TOk) value;
    }

    public TError getError() {
        if (isOk) {
            throw new IllegalStateException("isOk == true");
        }
        return (TError) value;
    }

    public boolean isOk() {
        return isOk;
    }

    public void ifOk(Consumer<TOk> action) {
        if (isOk) {
            action.accept(getOk());
        }
    }

    public void ifError(Consumer<TError> action) {
        if (!isOk) {
            action.accept(getError());
        }
    }
}

