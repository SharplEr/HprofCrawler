package org.sharpler.hprofcrawler.entries;

import com.pholser.junit.quickcheck.From;
import com.pholser.junit.quickcheck.Property;
import com.pholser.junit.quickcheck.runner.JUnitQuickcheck;
import org.junit.jupiter.api.Assertions;
import org.junit.runner.RunWith;
import org.sharpler.hprofcrawler.Utils;

@RunWith(JUnitQuickcheck.class)
public class FieldEntryPropertyTest {
    @Property
    public void serialize(@From(FieldEntryGenerator.class) FieldEntry entry) {
        Assertions.assertEquals(entry, Utils.deserialize(Utils.serialize(entry), FieldEntry.class));
    }
}
