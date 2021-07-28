package org.sharpler.hprofcrawler.views;

import org.sharpler.hprofcrawler.api.Progress;

public final class TerminalProgress implements Progress {
    private volatile int lastValue = -1;

    @Override
    public void setValue(int value) {
        if (value == lastValue) {
            return;
        }
        lastValue = value;
        // "|=====     |50%\r"
        StringBuilder sb = new StringBuilder(17);
        sb.append('|');
        for (int i = 0; i < 10; i++) {
            sb.append(i < (value / 10) ? '=' : ' ');
        }
        sb.append('|').append(value).append("%\r");
        System.out.print(sb);
    }

    @Override
    public void done() {
        System.out.println("done!            ");
    }
}
