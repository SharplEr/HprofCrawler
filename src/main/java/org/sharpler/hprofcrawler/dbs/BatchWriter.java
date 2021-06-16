package org.sharpler.hprofcrawler.dbs;

import org.iq80.leveldb.WriteBatch;

import javax.annotation.Nullable;
import java.io.IOException;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class BatchWriter {
    private final Supplier<? extends WriteBatch> generator;
    private final Consumer<? super WriteBatch> flusher;
    private final int limit;

    private int currentValue = 0;

    @Nullable
    private WriteBatch currentBatch = null;

    public BatchWriter(
            Supplier<? extends WriteBatch> generator,
            Consumer<? super WriteBatch> flusher,
            int limit)
    {
        this.generator = generator;
        this.flusher = flusher;
        this.limit = limit;

        assert limit > 1;
    }

    public final void add(byte[] key, byte[] value) {
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
        currentBatch.put(key, value);
        currentValue++;
    }

    public final void flush() {
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

