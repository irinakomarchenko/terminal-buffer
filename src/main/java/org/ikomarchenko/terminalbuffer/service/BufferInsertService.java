package org.ikomarchenko.terminalbuffer.service;

import org.ikomarchenko.terminalbuffer.buffer.Screen;
import org.ikomarchenko.terminalbuffer.buffer.ScrollbackBuffer;
import org.ikomarchenko.terminalbuffer.model.Cell;
import org.ikomarchenko.terminalbuffer.model.Cursor;
import org.ikomarchenko.terminalbuffer.model.Line;
import org.ikomarchenko.terminalbuffer.model.TextAttributes;

public final class BufferInsertService {

    public void insert(Screen screen,
                       ScrollbackBuffer scrollback,
                       Cursor cursor,
                       TextAttributes attributes,
                       String text) {

        requireScreen(screen);
        requireScrollback(scrollback);
        requireCursor(cursor);
        requireAttributes(attributes);
        requireText(text);

        for (char ch : text.toCharArray()) {
            insertCharacter(screen, scrollback, cursor, attributes, ch);
        }
    }

    private void insertCharacter(Screen screen,
                                 ScrollbackBuffer scrollback,
                                 Cursor cursor,
                                 TextAttributes attributes,
                                 char ch) {

        Cell insertedCell = new Cell(ch, attributes);

        Cell overflow = insertCellIntoLine(
                screen.getLine(cursor.getRow()),
                cursor.getColumn(),
                insertedCell
        );

        propagateOverflow(screen, scrollback, cursor.getRow() + 1, overflow);

        advanceCursorAfterInsert(screen, cursor);
    }

    private Cell insertCellIntoLine(Line line, int column, Cell cell) {

        Cell overflow = line.getCell(line.length() - 1);

        for (int i = line.length() - 1; i > column; i--) {
            line.setCell(i, line.getCell(i - 1));
        }

        line.setCell(column, cell);

        return overflow;
    }

    private void propagateOverflow(Screen screen,
                                   ScrollbackBuffer scrollback,
                                   int startRow,
                                   Cell overflowCell) {

        Cell currentOverflow = overflowCell;
        int row = startRow;

        while (true) {

            while (row < screen.getHeight()) {
                currentOverflow = insertCellIntoLine(
                        screen.getLine(row),
                        0,
                        currentOverflow
                );
                row++;
            }

            if (currentOverflow.isEmpty()) {
                return;
            }

            scrollScreen(screen, scrollback);

            row = screen.getHeight() - 1;

            currentOverflow = insertCellIntoLine(
                    screen.getLine(row),
                    0,
                    currentOverflow
            );

            row++;
        }
    }

    private void advanceCursorAfterInsert(Screen screen, Cursor cursor) {

        int lastColumn = screen.getWidth() - 1;
        int lastRow = screen.getHeight() - 1;

        if (cursor.getColumn() < lastColumn) {
            cursor.setPosition(cursor.getColumn() + 1, cursor.getRow());
            return;
        }

        if (cursor.getRow() < lastRow) {
            cursor.setPosition(0, cursor.getRow() + 1);
            return;
        }

        cursor.setPosition(0, lastRow);
    }

    private void scrollScreen(Screen screen, ScrollbackBuffer scrollback) {

        Line removedTopLine = screen.removeTopLine();
        scrollback.addLine(removedTopLine);
        screen.addLineAtBottom(new Line(screen.getWidth()));
    }

    private void requireScreen(Screen screen) {
        if (screen == null) {
            throw new IllegalArgumentException("Screen must not be null");
        }
    }

    private void requireScrollback(ScrollbackBuffer scrollback) {
        if (scrollback == null) {
            throw new IllegalArgumentException("Scrollback buffer must not be null");
        }
    }

    private void requireCursor(Cursor cursor) {
        if (cursor == null) {
            throw new IllegalArgumentException("Cursor must not be null");
        }
    }

    private void requireAttributes(TextAttributes attributes) {
        if (attributes == null) {
            throw new IllegalArgumentException("Text attributes must not be null");
        }
    }

    private void requireText(String text) {
        if (text == null) {
            throw new IllegalArgumentException("Text must not be null");
        }
    }
}
