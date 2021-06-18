package org.sharpler.hprofcrawler.entries;

import java.util.List;

import com.pholser.junit.quickcheck.From;
import com.pholser.junit.quickcheck.Property;
import com.pholser.junit.quickcheck.runner.JUnitQuickcheck;
import org.junit.jupiter.api.Assertions;
import org.junit.runner.RunWith;
import org.sharpler.hprofcrawler.ValuesGenerator;
import org.sharpler.hprofcrawler.parser.Value;

@RunWith(JUnitQuickcheck.class)
public class InstanceEntryPropertyTest {
    @Property
    public void serialize(long objectId, long classId, List<@From(ValuesGenerator.class) Value> fields) {
        InstanceEntry entry = new InstanceEntry(objectId, classId, fields);

        Assertions.assertEquals(entry, InstanceEntry.deserialize(entry.serialize()));
    }
}
