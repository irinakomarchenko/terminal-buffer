package org.ikomarchenko.terminalbuffer.service;

import org.ikomarchenko.terminalbuffer.buffer.Screen;
import org.ikomarchenko.terminalbuffer.buffer.ScrollbackBuffer;
import org.ikomarchenko.terminalbuffer.model.Cell;
import org.ikomarchenko.terminalbuffer.model.Cursor;
import org.ikomarchenko.terminalbuffer.model.Line;
import org.ikomarchenko.terminalbuffer.model.TextAttributes;

public final class BufferWriteService {

    public void write(Screen screen,
                                ScrollbackBuffer scrollback,
                                Cursor cursor,
                                TextAttributes attributes,
                                String text) {
        if (screen == null) {
            throw new IllegalArgumentException("Screen must not be null");
        }
        if (scrollback == null) {
            throw new IllegalArgumentException("Scrollback buffer must not be null");
        }
        if (cursor == null) {
            throw new IllegalArgumentException("Cursor must not be null");
        }
        if (attributes == null) {
            throw new IllegalArgumentException("Text attributes must not be null");
        }
        if (text == null) {
            throw new IllegalArgumentException("Text must not be null");
        }

        for (char ch : text.toCharArray()) {
            writeCharacter(screen, scrollback, cursor, attributes, ch);
        }
    }

    private void writeCharacter(Screen screen,
                ScrollbackBuffer scrollback,
                Cursor cursor,
                TextAttributes attributes,
                 char ch) {
        Line currentLine = screen.getLine(cursor.getRow());
        currentLine.setCell(cursor.getColumn(), new Cell(ch, attributes));

        advanceCursor(screen, scrollback, cursor);
    }

    private void advanceCursor(Screen screen, ScrollbackBuffer scrollback, Cursor cursor) {

        int lastColumn = screen.getWidth() - 1;
        int lastRow = screen.getHeight() - 1;

        if (cursor.getColumn() <  lastColumn) {
            cursor.setPosition(cursor.getColumn() + 1, cursor.getRow()  );
            return;
        }

        if (cursor.getRow() <  lastRow) {
            cursor.setPosition(0, cursor.getRow() + 1);
            return;
        }

        scrollScreen(screen, scrollback);
        cursor.setPosition(0, lastRow);
    }

        private void scrollScreen(Screen screen, ScrollbackBuffer scrollback) {
            Line removedTopLine = screen.removeTopLine();
            scrollback.addLine(removedTopLine);
            screen.addLineAtBottom(new Line(screen.getWidth()));
    }
}
