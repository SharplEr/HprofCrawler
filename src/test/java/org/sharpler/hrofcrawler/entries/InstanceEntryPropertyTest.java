package org.sharpler.hrofcrawler.entries;

import java.util.List;

import com.pholser.junit.quickcheck.From;
import com.pholser.junit.quickcheck.Property;
import com.pholser.junit.quickcheck.runner.JUnitQuickcheck;
import org.junit.jupiter.api.Assertions;
import org.junit.runner.RunWith;
import org.sharpler.hrofcrawler.Utils;
import org.sharpler.hrofcrawler.ValuesGenerator;
import org.sharpler.hrofcrawler.parser.Value;

@RunWith(JUnitQuickcheck.class)
public class InstanceEntryPropertyTest {
    @Property
    public void serialize(long objectId, long classId, List<@From(ValuesGenerator.class) Value> fields) {
        InstanceEntry entry = new InstanceEntry(objectId, classId, fields);

        Assertions.assertEquals(entry, Utils.deserialize(Utils.serialize(entry), InstanceEntry.class));
    }
}
