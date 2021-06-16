package org.sharpler.hprofcrawler.entries;

import com.pholser.junit.quickcheck.From;
import com.pholser.junit.quickcheck.Property;
import com.pholser.junit.quickcheck.runner.JUnitQuickcheck;
import org.junit.jupiter.api.Assertions;
import org.junit.runner.RunWith;
import org.sharpler.hprofcrawler.Utils;

import java.util.List;

@RunWith(JUnitQuickcheck.class)
public class ClassEntryPropertyTest {
    @Property
    public void serialize(
            String name,
            long id,
            long superClassId,
            int instanceSize,
            List<@From(FieldEntryGenerator.class) FieldEntry> fields,
            int count)
    {
        ClassEntry entry = new ClassEntry(name, id, superClassId, instanceSize, fields, count);

        Assertions.assertEquals(entry, Utils.deserialize(Utils.serialize(entry), ClassEntry.class));
    }
}