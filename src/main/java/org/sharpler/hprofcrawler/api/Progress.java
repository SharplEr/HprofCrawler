package org.sharpler.hprofcrawler.api;

/**
 * Interface for reporting progress.
 *
 * @implSpec Use it only for logging or UI progress bars.
 * Calling {{@link #done()}} has no happens-before relations with finishing operation
 * and some writes could still be unobservable.
 */
public interface Progress {
    /**
     * Set current progress as a percentage.
     *
     * @param value value from {@code 0} to {100}
     */
    void setValue(int value);

    /**
     * Reporting about finishing work.
     */
    void done();

    /**
     * No-op implementation.
     */
    final class Dummy implements Progress {

        @Override
        public void setValue(int value) {
            // No-op.
        }

        @Override
        public void done() {
            // No-op.
        }

    }
}
