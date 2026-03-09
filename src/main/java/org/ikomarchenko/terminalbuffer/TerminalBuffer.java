package org.ikomarchenko.terminalbuffer;

import org.ikomarchenko.terminalbuffer.buffer.Screen;
import org.ikomarchenko.terminalbuffer.buffer.ScrollbackBuffer;
import org.ikomarchenko.terminalbuffer.model.Cursor;
import org.ikomarchenko.terminalbuffer.model.TextAttributes;

public final class TerminalBuffer {

    private final int width;
    private final int height;

    private final Screen screen;
    private final ScrollbackBuffer scrollback;

    private final Cursor cursor;

    private TextAttributes currentAttributes;

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
        this.screen = new Screen(width, height);
        this.scrollback = new ScrollbackBuffer(scrollbackLimit);
        this.cursor = new Cursor(0, 0);
        this.currentAttributes = TextAttributes.defaultAttributes();
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public int getScrollbackLimit() {
        return scrollback.getLimit();
    }

    public Screen getScreen() {
        return screen;
    }

    public ScrollbackBuffer getScrollback() {
        return scrollback;
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
}
