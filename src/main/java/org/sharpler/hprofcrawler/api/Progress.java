package org.sharpler.hprofcrawler.api;

public interface Progress {
    void setValue(int value);

    void done();

    final class Dummy implements Progress {

        @Override
        public void setValue(int value) {

        }

        @Override
        public void done() {

        }

    }
}
