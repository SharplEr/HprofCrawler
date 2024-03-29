package org.sharpler.hprofcrawler;

import com.pholser.junit.quickcheck.Property;
import com.pholser.junit.quickcheck.runner.JUnitQuickcheck;
import org.junit.jupiter.api.Assertions;
import org.junit.runner.RunWith;

import java.util.function.Supplier;

@RunWith(JUnitQuickcheck.class)
public class UtilsTest {

    @Property
    public void serializeLong(long value) {
        Assertions.assertEquals(value, Utils.deserializeLong(Utils.serializeLong(value)));
    }

    @Property
    public void resourceOwner(boolean aFailed, boolean bFailed, boolean cFailed, boolean dFailed, boolean eFailed, boolean fFailed) {
        Supplier<String> supplier = () -> Utils.resourceOwner(
                (a, b, c, d, e, f) -> String.format("%s:%s:%s:%s:%s:%s", a.getText(), b.getText(), c.getText(), d.getText(), e.getText(), f.getText()),
                () -> throwIf("a", aFailed),
                () -> throwIf("b", bFailed),
                () -> throwIf("c", cFailed),
                () -> throwIf("d", dFailed),
                () -> throwIf("e", eFailed),
                () -> throwIf("f", fFailed)
        );

        if (aFailed || bFailed || cFailed || dFailed || eFailed || fFailed) {
            Assertions.assertThrows(RuntimeException.class, supplier::get);
        } else {
            Assertions.assertEquals("a:b:c:d:e:f", supplier.get());
        }
    }

    private static Resource throwIf(String text, boolean failed) {
        if (failed) {
            throw new RuntimeException(text);
        }

        return new Resource(text);
    }

    static final class Resource implements AutoCloseable {
        private final String text;

        Resource(String text) {
            this.text = text;
        }

        String getText() {
            return text;
        }

        @Override
        public void close() {

        }
    }
}