package org.sharpler.hprofcrawler.inspection;

import org.sharpler.hprofcrawler.api.Progress;
import org.sharpler.hprofcrawler.backend.Backend;

public interface Inspection {
    String run(Backend backend, Progress progress);
}
