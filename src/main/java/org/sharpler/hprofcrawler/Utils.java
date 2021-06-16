package org.sharpler.hprofcrawler;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import org.fusesource.leveldbjni.JniDBFactory;
import org.iq80.leveldb.DB;
import org.iq80.leveldb.Options;
import org.jooq.lambda.Unchecked;
import org.jooq.lambda.function.Function4;
import org.sharpler.hprofcrawler.entries.InstanceEntry;
import org.sharpler.hprofcrawler.views.InstanceView;

import javax.annotation.Nullable;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.function.ToLongFunction;
import java.util.stream.Collector;
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

    public static byte[] serializeInstanceView(InstanceView view) {
        return serialize(
                new InstanceEntry(view.getObjId(), view.getClassView().getId(), view.getFields())
        );
    }

    public static byte[] serialize(Object object) {
        try {
            return MAPPER.writeValueAsBytes(object);
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException(e);
        }
    }

    public static <T> T deserialize(byte[] data, Class<T> clazz) {
        try {
            return MAPPER.readValue(data, clazz);
        } catch (IOException e) {
            throw new IllegalArgumentException(e);
        }
    }

    public static String toPrettyString(Object object) {
        try {
            return MAPPER.writerWithDefaultPrettyPrinter().writeValueAsString(object);
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException(e);
        }
    }

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

    @SuppressWarnings("OverlyComplexBooleanExpression")
    public static long deserializeSecondLong(byte[] bytes) {
        return (bytes[8] & 0xFFL) << 56
                | (bytes[9] & 0xFFL) << 48
                | (bytes[10] & 0xFFL) << 40
                | (bytes[11] & 0xFFL) << 32
                | (bytes[12] & 0xFFL) << 24
                | (bytes[13] & 0xFFL) << 16
                | (bytes[14] & 0xFFL) << 8
                | (bytes[15] & 0xFFL);
    }

    public static <V, E> Collector<E, ?, Long2ObjectOpenHashMap<V>>
    toLong2ObjectOpenHashMap(
            ToLongFunction<? super E> keyMapper,
            Function<? super E, ? extends V> valueMapper) {
        return Collector.of(
                Long2ObjectOpenHashMap::new,
                (map, x) -> map.put(keyMapper.applyAsLong(x), valueMapper.apply(x)),
                (map1, map2) -> {
                    map1.putAll(map2);
                    return map1;
                }
        );
    }

    @Nullable
    public static Void nullTs() {
        return null;
    }

    public static <
            A extends AutoCloseable,
            B extends AutoCloseable,
            C extends AutoCloseable,
            D extends AutoCloseable,
            R>
    R resourceOwner(
            Function4<A, B, C, D, R> ownerBuilder,
            Supplier<A> aSupplier,
            Supplier<B> bSupplier,
            Supplier<C> cSupplier,
            Supplier<D> dSupplier
    ) {
        A a = null;
        B b = null;
        C c = null;
        D d = null;
        try {
            a = aSupplier.get();
            b = bSupplier.get();
            c = cSupplier.get();
            d = dSupplier.get();

            return ownerBuilder.apply(a, b, c, d);
        } catch (Exception e) {
            Stream.of(a, b, c, d)
                    .filter(Objects::nonNull)
                    .flatMap(Utils::safeClose)
                    .forEach(e::addSuppressed);
            throw e;
        }
    }
}
