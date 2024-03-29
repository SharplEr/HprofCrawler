package org.sharpler.hprofcrawler;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import it.unimi.dsi.fastutil.bytes.ByteArrayList;
import org.fusesource.leveldbjni.JniDBFactory;
import org.iq80.leveldb.DB;
import org.iq80.leveldb.Options;
import org.jooq.lambda.Unchecked;
import org.jooq.lambda.function.Function3;
import org.jooq.lambda.function.Function6;

import javax.annotation.Nullable;
import java.nio.file.Path;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public final class Utils {
    private static final ObjectMapper MAPPER = new ObjectMapper();
    private static final Options LEVEL_DB_OPTIONS = new Options()
            .createIfMissing(true)
            .writeBufferSize(16 * 1024 * 1024);

    static {
        MAPPER.setVisibility(MAPPER.getSerializationConfig().getDefaultVisibilityChecker()
                .withFieldVisibility(JsonAutoDetect.Visibility.ANY)
                .withGetterVisibility(JsonAutoDetect.Visibility.NONE)
                .withSetterVisibility(JsonAutoDetect.Visibility.NONE));
    }

    private Utils() {
        // No-op.
    }

    public static byte toByteExact(int x) {
        byte result = (byte) x;
        if (result != x) {
            throw new ArithmeticException("integer overflow");
        }
        return result;
    }

    public static DB openDb(Path path) {
        try {
            return JniDBFactory.factory.open(path.toFile(), LEVEL_DB_OPTIONS);
        } catch (Exception e) {
            Unchecked.THROWABLE_TO_RUNTIME_EXCEPTION.accept(e);
            throw new IllegalStateException("Exception handler must throw a RuntimeException", e);
        }
    }

    public static Stream<Exception> safeClose(AutoCloseable resource) {
        try {
            resource.close();
            return Stream.empty();
        } catch (Exception e) {
            return Stream.of(e);
        }
    }

    public static void closeAll(AutoCloseable... resources) {
        List<Exception> exceptions = Stream.of(resources)
                .flatMap(Utils::safeClose)
                .collect(Collectors.toList());

        if (!exceptions.isEmpty()) {
            RuntimeException exception = new RuntimeException("Failed to close all resources");
            exceptions.forEach(exception::addSuppressed);
            throw exception;
        }
    }

    public static String toPrettyString(Object object) {
        try {
            return MAPPER.writerWithDefaultPrettyPrinter().writeValueAsString(object);
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException(e);
        }
    }

    @SuppressWarnings("NumericCastThatLosesPrecision")
    public static byte[] serializeLong(long data) {
        return new byte[]{
                (byte) ((data >> 56) & 0xff),
                (byte) ((data >> 48) & 0xff),
                (byte) ((data >> 40) & 0xff),
                (byte) ((data >> 32) & 0xff),
                (byte) ((data >> 24) & 0xff),
                (byte) ((data >> 16) & 0xff),
                (byte) ((data >> 8) & 0xff),
                (byte) ((data) & 0xff),
        };
    }

    public static void addAll(ByteArrayList list, byte[] bytes) {
        for (var x : bytes) {
            list.add(x);
        }
    }

    @SuppressWarnings("NumericCastThatLosesPrecision")
    public static byte[] serializeTwoLong(long x, long y) {
        return new byte[]{
                (byte) ((x >> 56) & 0xff),
                (byte) ((x >> 48) & 0xff),
                (byte) ((x >> 40) & 0xff),
                (byte) ((x >> 32) & 0xff),
                (byte) ((x >> 24) & 0xff),
                (byte) ((x >> 16) & 0xff),
                (byte) ((x >> 8) & 0xff),
                (byte) ((x) & 0xff),
                (byte) ((y >> 56) & 0xff),
                (byte) ((y >> 48) & 0xff),
                (byte) ((y >> 40) & 0xff),
                (byte) ((y >> 32) & 0xff),
                (byte) ((y >> 24) & 0xff),
                (byte) ((y >> 16) & 0xff),
                (byte) ((y >> 8) & 0xff),
                (byte) ((y) & 0xff),
        };
    }

    @SuppressWarnings("OverlyComplexBooleanExpression")
    public static long deserializeLong(byte[] bytes) {
        return (bytes[0] & 0xFFL) << 56
                | (bytes[1] & 0xFFL) << 48
                | (bytes[2] & 0xFFL) << 40
                | (bytes[3] & 0xFFL) << 32
                | (bytes[4] & 0xFFL) << 24
                | (bytes[5] & 0xFFL) << 16
                | (bytes[6] & 0xFFL) << 8
                | (bytes[7] & 0xFFL);
    }

    @Nullable
    public static Void nullTs() {
        return null;
    }


    public static <
            A extends AutoCloseable,
            B extends AutoCloseable,
            C extends AutoCloseable,
            R>
    R resourceOwner(
            Function3<? super A, ? super B, ? super C, ? extends R> ownerBuilder,
            Supplier<? extends A> aSupplier,
            Supplier<? extends B> bSupplier,
            Supplier<? extends C> cSupplier
    ) {
        A a = null;
        B b = null;
        C c = null;
        try {
            a = aSupplier.get();
            b = bSupplier.get();
            c = cSupplier.get();

            return ownerBuilder.apply(a, b, c);
        } catch (Exception exp) {
            Stream.of(a, b, c)
                    .filter(Objects::nonNull)
                    .flatMap(Utils::safeClose)
                    .forEach(exp::addSuppressed);
            throw exp;
        }
    }

    public static <
            A extends AutoCloseable,
            B extends AutoCloseable,
            C extends AutoCloseable,
            D extends AutoCloseable,
            E extends AutoCloseable,
            F extends AutoCloseable,
            R>
    R resourceOwner(
            Function6<? super A, ? super B, ? super C, ? super D, ? super E, ? super F, ? extends R> ownerBuilder,
            Supplier<? extends A> aSupplier,
            Supplier<? extends B> bSupplier,
            Supplier<? extends C> cSupplier,
            Supplier<? extends D> dSupplier,
            Supplier<? extends E> eSupplier,
            Supplier<? extends F> fSupplier
    ) {
        A a = null;
        B b = null;
        C c = null;
        D d = null;
        E e = null;
        F f = null;
        try {
            a = aSupplier.get();
            b = bSupplier.get();
            c = cSupplier.get();
            d = dSupplier.get();
            e = eSupplier.get();
            f = fSupplier.get();

            return ownerBuilder.apply(a, b, c, d, e, f);
        } catch (Exception exp) {
            Stream.of(a, b, c, d, e, f)
                    .filter(Objects::nonNull)
                    .flatMap(Utils::safeClose)
                    .forEach(exp::addSuppressed);
            throw exp;
        }
    }

    @Nullable
    public static <T, U> U map(@Nullable T val, Function<? super T, ? extends U> mapper) {
        if (val == null) {
            return null;
        }
        return mapper.apply(val);
    }
}
