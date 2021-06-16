package org.sharpler.hprofcrawler;

import com.pholser.junit.quickcheck.generator.GenerationStatus;
import com.pholser.junit.quickcheck.generator.Generator;
import com.pholser.junit.quickcheck.random.SourceOfRandomness;
import org.sharpler.hprofcrawler.parser.Type;
import org.sharpler.hprofcrawler.parser.Value;

public final class ValuesGenerator extends Generator<Value> {

    public ValuesGenerator() {
        super(Value.class);
    }

    @Override
    public Value generate(SourceOfRandomness sourceOfRandomness, GenerationStatus generationStatus) {
        switch (gen().type(Type.class).generate(sourceOfRandomness, generationStatus)) {
            case OBJ:
                return Value.ofLong(
                        gen().type(long.class).generate(sourceOfRandomness, generationStatus),
                        true
                );
            case BOOL:
                return Value.ofBool(
                        gen().type(boolean.class).generate(sourceOfRandomness, generationStatus)
                );
            case CHAR:
                return Value.ofChar(
                        gen().type(char.class).generate(sourceOfRandomness, generationStatus)
                );
            case FLOAT:
                return Value.ofFloat(
                        gen().type(float.class).generate(sourceOfRandomness, generationStatus)
                );
            case DOUBLE:
                return Value.ofDouble(
                        gen().type(double.class).generate(sourceOfRandomness, generationStatus)
                );
            case BYTE:
                return Value.ofByte(
                        gen().type(byte.class).generate(sourceOfRandomness, generationStatus)
                );
            case SHORT:
                return Value.ofShort(
                        gen().type(short.class).generate(sourceOfRandomness, generationStatus)
                );
            case INT:
                return Value.ofInt(
                        gen().type(short.class).generate(sourceOfRandomness, generationStatus)
                );
            case LONG:
                return Value.ofLong(
                        gen().type(long.class).generate(sourceOfRandomness, generationStatus),
                        false
                );
        }
        throw new IllegalStateException();
    }
}
