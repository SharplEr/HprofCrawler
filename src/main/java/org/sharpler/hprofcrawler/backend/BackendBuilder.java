package org.sharpler.hprofcrawler.backend;

import org.sharpler.hprofcrawler.api.Progress;

public interface BackendBuilder {
    Backend build(Progress progress);
}
