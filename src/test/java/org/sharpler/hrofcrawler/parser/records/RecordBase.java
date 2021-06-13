package org.sharpler.hrofcrawler.parser.records;

import java.io.DataOutputStream;
import java.io.IOException;

public interface RecordBase {
    void storeInto(DataOutputStream output) throws IOException;
}
