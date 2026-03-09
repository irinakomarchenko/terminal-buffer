package org.ikomarchenko.terminalbuffer;

import org.ikomarchenko.terminalbuffer.model.Cursor;
import org.ikomarchenko.terminalbuffer.model.Line;
import org.ikomarchenko.terminalbuffer.model.TextAttributes;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

public final class TerminalBuffer {

    private final  int width;
    private final int height;
    private final  int scrollbackLimit;

    private final  List<Line> screen;
    private final Deque<Line> scrollback;

    private final Cursor cursor;

    private  TextAttributes currentAttributes;

    public TerminalBuffer(int width, int height, int scrollbackLimit) {

        if (width <= 0) {
            throw new IllegalArgumentException("Width must be greater than 0");
        }
        if (height <= 0) {
            throw new IllegalArgumentException("Height must be greater than 0");
        }
        if (scrollbackLimit < 0) {
            throw new IllegalArgumentException("Scrollback limit must not be negative");
        }

        this.width = width;
        this.height = height;
        this.scrollbackLimit = scrollbackLimit;

        this.screen = new ArrayList<>(height);
        this.scrollback = new ArrayDeque<>(scrollbackLimit);

        initializeScreen();

        this.cursor = new Cursor(0, 0);

        this.currentAttributes = TextAttributes.defaultAttributes();
    }

    private void initializeScreen() {
        for (int i = 0; i < height; i++) {
            screen.add(new Line(width));
        }
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public int getScrollbackLimit() {
        return scrollbackLimit;
    }

    public Cursor getCursor() {
        return cursor;
    }

    public TextAttributes getCurrentAttributes() {
        return currentAttributes;
    }

    public void setCurrentAttributes(TextAttributes attributes) {
        if (attributes == null) {
            throw new IllegalArgumentException("Attributes must not be null");
        }

        this.currentAttributes = attributes;
    }

    public List<Line> getScreen() {
        return screen;
    }

    public Deque<Line> getScrollback() {
        return scrollback;
    }
}
