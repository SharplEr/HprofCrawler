package org.sharpler.hprofcrawler.entries;

import com.pholser.junit.quickcheck.generator.GenerationStatus;
import com.pholser.junit.quickcheck.generator.Generator;
import com.pholser.junit.quickcheck.random.SourceOfRandomness;
import org.sharpler.hprofcrawler.parser.Type;

public final class FieldEntryGenerator extends Generator<FieldEntry> {
    public FieldEntryGenerator() {
        super(FieldEntry.class);
    }

    @Override
    public FieldEntry generate(SourceOfRandomness sourceOfRandomness, GenerationStatus generationStatus) {
        return new FieldEntry(
                gen().type(String.class).generate(sourceOfRandomness, generationStatus),
                gen().type(Type.class).generate(sourceOfRandomness, generationStatus)
        );
    }
}
