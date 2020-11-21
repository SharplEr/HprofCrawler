package org.sharpler.hrofcrawler.api;

public interface Progress {
    public void setValue(int value);

    public void reset();

    public static final class Dummy implements Progress {

        @Override
        public void setValue(int value) {

        }

        @Override
        public void reset() {

        }
    }
}
