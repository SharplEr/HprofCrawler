package org.sharpler.hprofcrawler.dbs;

import org.iq80.leveldb.WriteBatch;

import javax.annotation.Nullable;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class BatchWriter {
    private final Supplier<? extends WriteBatch> generator;
    private final Consumer<? super WriteBatch> flusher;

    private final long sizeLimit;

    private long currentSize = 0;

    @Nullable
    private WriteBatch currentBatch = null;

    public BatchWriter(Supplier<? extends WriteBatch> generator, Consumer<? super WriteBatch> flusher) {
        this.generator = generator;
        this.flusher = flusher;
        this.sizeLimit = 100L * 1024L * 1024L;
    }

    public final void add(byte[] key, byte[] value) {
        if (currentBatch == null) {
            currentBatch = generator.get();
            currentSize = 0L;
        } else if (currentSize + value.length + key.length >= sizeLimit) {
            flusher.accept(currentBatch);
            try {
                currentBatch.close();
            } catch (IOException e) {
                throw new UncheckedIOException(e);
            }
            currentBatch = generator.get();
            currentSize = 0L;
        }
        currentBatch.put(key, value);
        currentSize += (value.length + key.length);
    }

    public final void flush() {
        if (currentBatch != null) {
            flusher.accept(currentBatch);
            try {
                currentBatch.close();
            } catch (IOException e) {
                throw new UncheckedIOException(e);
            }
            currentSize = 0L;
            currentBatch = null;
        }
    }
}

