package org.ikomarchenko.terminalbuffer;

import org.ikomarchenko.terminalbuffer.buffer.Screen;
import org.ikomarchenko.terminalbuffer.buffer.ScrollbackBuffer;
import org.ikomarchenko.terminalbuffer.model.Cursor;
import org.ikomarchenko.terminalbuffer.model.TextAttributes;
import org.ikomarchenko.terminalbuffer.service.BufferMaintenanceService;
import org.ikomarchenko.terminalbuffer.service.CursorService;
import org.ikomarchenko.terminalbuffer.service.BufferWriteService;
import org.ikomarchenko.terminalbuffer.service.BufferContentService;


public final class TerminalBuffer {

    private final int width;
    private final int height;

    private final Screen screen;
    private final ScrollbackBuffer scrollback;

    private final Cursor cursor;
    private final CursorService cursorService;
    private final BufferWriteService bufferWriteService;
    private final BufferMaintenanceService bufferMaintenanceService;
    private final BufferContentService bufferContentService;


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
        this.cursorService = new CursorService();
        this.bufferWriteService = new BufferWriteService();
        this.bufferMaintenanceService = new BufferMaintenanceService();
        this.bufferContentService = new BufferContentService();
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

    public void setCursor(int column, int row) {
        cursorService.setCursor(cursor, column, row, width, height);
    }

    public void moveCursorUp(int cells) {
        cursorService.moveUp(cursor, cells, width, height);
    }

    public void moveCursorDown(int cells) {
        cursorService.moveDown(cursor, cells, width, height);
    }

    public void moveCursorLeft(int cells) {
        cursorService.moveLeft(cursor, cells, width, height);
    }

    public void moveCursorRight(int cells) {
        cursorService.moveRight(cursor, cells, width, height);
    }

    public void write(String text) {
        bufferWriteService.write(screen, scrollback, cursor, currentAttributes, text);
    }

    public void fillLine(char character) {
        bufferWriteService.fillLine(screen, cursor, currentAttributes, character);
    }

    public void insertEmptyLineAtBottom() {
        bufferMaintenanceService.insertEmptyLineAtBottom(screen, scrollback);
    }

    public void clearScreen() {
        bufferMaintenanceService.clearScreen(screen);
    }

    public void clearScreenAndScrollback() {
        bufferMaintenanceService.clearScreenAndScrollback(screen, scrollback);
    }

    public char getCharacterAt(int row, int column) {
        return bufferContentService.getCharacterAt(screen, scrollback, row, column);
    }

    public TextAttributes getAttributesAt(int row, int column) {
        return bufferContentService.getAttributesAt(screen, scrollback, row, column);
    }

    public String getLineAsString(int row) {
        return bufferContentService.getLineAsString(screen, scrollback, row);
    }

    public String getScreenContentAsString() {
        return bufferContentService.getScreenContentAsString(screen);
    }

    public String getBufferContentAsString() {
        return bufferContentService.getBufferContentAsString(screen, scrollback);
    }
}
