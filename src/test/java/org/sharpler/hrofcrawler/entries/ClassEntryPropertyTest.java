package org.sharpler.hrofcrawler.entries;

import java.util.List;

import com.pholser.junit.quickcheck.From;
import com.pholser.junit.quickcheck.Property;
import com.pholser.junit.quickcheck.runner.JUnitQuickcheck;
import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.sharpler.hrofcrawler.Utils;

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