package org.sharpler.hprofcrawler.entries;

import com.pholser.junit.quickcheck.generator.GenerationStatus;
import com.pholser.junit.quickcheck.generator.Generator;
import com.pholser.junit.quickcheck.random.SourceOfRandomness;
import org.sharpler.hprofcrawler.parser.InstanceField;
import org.sharpler.hprofcrawler.parser.Type;

public final class FieldEntryGenerator extends Generator<InstanceField> {
    public FieldEntryGenerator() {
        super(InstanceField.class);
    }

    @Override
    public InstanceField generate(SourceOfRandomness sourceOfRandomness, GenerationStatus generationStatus) {
        return new InstanceField(
                gen().type(int.class).generate(sourceOfRandomness, generationStatus),
                gen().type(Type.class).generate(sourceOfRandomness, generationStatus)
        );
    }
}
