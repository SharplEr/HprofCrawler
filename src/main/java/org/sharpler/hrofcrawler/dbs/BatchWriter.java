package org.sharpler.hrofcrawler.dbs;

import org.checkerframework.checker.nullness.qual.Nullable;
import org.iq80.leveldb.WriteBatch;

import java.io.IOException;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class BatchWriter {
    private final Supplier<WriteBatch> generator;
    private final Consumer<WriteBatch> flusher;
    private final int limit;

    private int currentValue = 0;
    @Nullable
    private WriteBatch currentBatch = null;


    public BatchWriter(
            Supplier<WriteBatch> generator,
            Consumer<WriteBatch> flusher,
            int limit)
    {
        this.generator = generator;
        this.flusher = flusher;
        this.limit = limit;

        assert limit > 1;
    }

    private void updateIfNeed() {
        if (currentBatch == null) {
            currentBatch = generator.get();
            currentValue = 0;
        } else if (currentValue == limit) {
            flusher.accept(currentBatch);
            try {
                currentBatch.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            currentBatch = generator.get();
            currentValue = 0;
        }
    }

    public void add(byte[] key, byte[] value) {
        updateIfNeed();
        currentBatch.put(key, value);
        currentValue++;
    }

    public void flush() {
        if (currentBatch != null) {
            flusher.accept(currentBatch);
            try {
                currentBatch.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            currentValue = 0;
            currentBatch = null;
        }
    }
}

