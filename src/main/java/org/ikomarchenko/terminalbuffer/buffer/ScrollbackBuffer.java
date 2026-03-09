package org.ikomarchenko.terminalbuffer.buffer;

import org.ikomarchenko.terminalbuffer.model.Line;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.List;

public final class ScrollbackBuffer {

    private final int limit;
    private final Deque<Line> lines;

    public ScrollbackBuffer(int limit) {
        if (limit < 0) {
            throw new IllegalArgumentException("Scrollback limit must not be negative");
        }

        this.limit = limit;
        this.lines = new ArrayDeque<>(limit);
    }

    public int getLimit() {
        return limit;
    }

    public int size() {
        return lines.size();
    }

    public void addLine(Line line) {
        if (line == null) {
            throw new IllegalArgumentException("Line must not be null");
        }

        if (limit == 0) {
            return;
        }

        if (lines.size() == limit) {
            lines.removeFirst();
        }

        lines.addLast(line);
    }

    public void clear() {
        lines.clear();
    }

    public List<Line> getLines() {
        return Collections.unmodifiableList(new ArrayList<>(lines));
    }
}
