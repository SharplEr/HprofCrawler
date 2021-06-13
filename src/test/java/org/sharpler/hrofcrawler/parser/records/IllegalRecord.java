package org.sharpler.hrofcrawler.parser.records;

import java.io.DataOutputStream;
import java.io.IOException;

public final class IllegalRecord implements RecordBase {

    @Override
    public void storeInto(DataOutputStream output) throws IOException {
        output.writeByte(255);
    }
}
