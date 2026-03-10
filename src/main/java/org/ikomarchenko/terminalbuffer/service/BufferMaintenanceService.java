package org.ikomarchenko.terminalbuffer.service;

import org.ikomarchenko.terminalbuffer.buffer.Screen;
import org.ikomarchenko.terminalbuffer.buffer.ScrollbackBuffer;
import org.ikomarchenko.terminalbuffer.model.Line;

public final class BufferMaintenanceService {

    public void insertEmptyLineAtBottom(Screen screen, ScrollbackBuffer scrollback) {
        if (screen == null) {
            throw new IllegalArgumentException("Screen must not be null");
        }
        if (scrollback == null) {
            throw new IllegalArgumentException("ScrollbackBuffer must not be null");
        }

        Line removedTopLine = screen.removeTopLine();
        scrollback.addLine(removedTopLine);
        screen.addLineAtBottom(new Line(screen.getWidth()));
    }

    public void clearScreen(Screen screen) {
        if (screen == null) {
            throw new IllegalArgumentException("Screen must not be null");
        }

        screen.clear();
    }

    public void clearScreenAndScrollback(Screen screen, ScrollbackBuffer scrollback) {
        if (screen == null) {
            throw new IllegalArgumentException("Screen must not be null");
        }
        if (scrollback == null) {
            throw new IllegalArgumentException("Scrollback buffer must not be null");
        }

        screen.clear();
        scrollback.clear();
    }
}
