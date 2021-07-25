package org.sharpler.hprofcrawler.dbs;

import org.sharpler.hprofcrawler.api.Progress;

public interface Database extends AutoCloseable {
    void compact();


    static void compactAll(Progress progress, Database... databases) {
        for (int i = 0; i < databases.length; i++) {
            databases[i].compact();
            progress.setValue(100 * i / databases.length);
        }
    }
}
