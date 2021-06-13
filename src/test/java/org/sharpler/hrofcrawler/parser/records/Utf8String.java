package org.sharpler.hrofcrawler.parser.records;

import org.sharpler.hrofcrawler.parser.HprofParserTest;

import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

public final class Utf8String implements RecordBase {
    private final int idSize;
    private final long id;
    private final String str;

    public Utf8String(int idSize, long id, String str) {
        this.idSize = idSize;
        this.id = id;
        this.str = str;
    }

    @Override
    public void storeInto(DataOutputStream output) throws IOException {
        output.writeByte(0x1);
        output.writeInt(0);
        byte[] bytes = str.getBytes(StandardCharsets.UTF_8);
        output.writeInt(bytes.length + idSize);
        HprofParserTest.storeIntoStream(idSize, id, output);
        output.write(bytes);
    }

    @Override
    public String toString() {
        return "Utf8String{" +
                "idSize=" + idSize +
                ", id=" + id +
                ", str='" + str + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Utf8String that = (Utf8String) o;

        if (idSize != that.idSize) return false;
        if (id != that.id) return false;
        return str.equals(that.str);
    }

    @Override
    public int hashCode() {
        int result = idSize;
        result = 31 * result + (int) (id ^ (id >>> 32));
        result = 31 * result + str.hashCode();
        return result;
    }
}
