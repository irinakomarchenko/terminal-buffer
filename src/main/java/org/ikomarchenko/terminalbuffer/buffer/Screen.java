package org.ikomarchenko.terminalbuffer.buffer;

import org.ikomarchenko.terminalbuffer.model.Line;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class Screen {

    private final int width;
    private final int height;
    private final List<Line> lines;

    public Screen(int width, int height) {
        if (width <= 0 ) {
            throw new IllegalArgumentException("Width must be greater than 0");
        }
        if (height <= 0 ) {
            throw new IllegalArgumentException("Height must be greater than 0");
        }

        this.width = width;
        this.height = height;
        this.lines = new ArrayList<>(height);

        initialize();
    }

    private void initialize() {
        for (int i = 0; i < height; i++) {
            lines.add(new Line(width));
        }
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public Line getLine(int row) {
        checkRowIndex(row);
        return lines.get(row);
    }

    public void setLine(int row, Line line) {
        checkRowIndex(row);

        if (line == null) {
            throw new IllegalArgumentException("Line must not be null");
        }

        lines.set(row, line);
    }

    public Line removeTopLine() {
        return lines.remove(0);
    }

    public void addLineAtBottom(Line line) {
        if (line == null) {
            throw new IllegalArgumentException("Line must not be null");
        }

        if (lines.size() >= height) {
            throw new IllegalStateException("Screen is already full");
        }

        lines.add(line);
    }

    public void clear() {
        lines.clear();
        initialize();
    }

    public List<Line> getLines() {
        return Collections.unmodifiableList(lines);
    }

    private void checkRowIndex(int row) {
        if (row < 0 || row >= lines.size()) {
            throw new IndexOutOfBoundsException(
                    "row index out of bounds: " + row + ", screen height: " + lines.size()
            );
        }
    }
}
