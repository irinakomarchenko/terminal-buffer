package org.ikomarchenko.terminalbuffer.service;

import org.ikomarchenko.terminalbuffer.buffer.Screen;
import org.ikomarchenko.terminalbuffer.buffer.ScrollbackBuffer;
import org.ikomarchenko.terminalbuffer.model.Cell;
import org.ikomarchenko.terminalbuffer.model.Line;
import org.ikomarchenko.terminalbuffer.model.TextAttributes;

import java.util.List;

public final class BufferContentService {

    public char getCharacterAt(Screen screen, ScrollbackBuffer scrollback, int row, int column) {
        Cell cell = getCellAt(screen, scrollback, row, column);
        return cell.getCharacter();
    }

    public TextAttributes getAttributesAt(Screen screen, ScrollbackBuffer scrollback, int row, int column) {
        Cell cell = getCellAt(screen, scrollback, row, column);
        return cell.getAttributes();
    }

    public String getLineAsString(Screen screen, ScrollbackBuffer scrollback, int row) {
        Line line = resolveLine(screen, scrollback, row);
        return line.asString();
    }

    public String getScreenContentAsString(Screen screen) {
        validateScreen(screen);

        StringBuilder builder = new StringBuilder();
        List<Line> screenLines = screen.getLines();

        for (int i = 0; i < screenLines.size(); i++) {
            builder.append(screenLines.get(i).asString());
            if (i < screenLines.size() - 1) {
                builder.append(System.lineSeparator());
            }
        }

        return builder.toString();
    }

    public String getBufferContentAsString(Screen screen, ScrollbackBuffer scrollback) {
        validateScreen(screen);
        validateScrollback(scrollback);

        StringBuilder builder = new StringBuilder();
        List<Line> scrollbackLines = scrollback.getLines();
        List<Line> screenLines = screen.getLines();

        for (Line line : scrollbackLines) {
            builder.append(line.asString()).append(System.lineSeparator());
        }

        for (int i = 0; i < screenLines.size(); i++) {
            builder.append(screenLines.get(i).asString());
            if (i < screenLines.size() - 1) {
                builder.append(System.lineSeparator());
            }
        }

        return builder.toString();
    }

    private Cell getCellAt(Screen screen, ScrollbackBuffer scrollback, int row, int column) {
        Line line = resolveLine(screen, scrollback, row);
        return line.getCell(column);
    }

    private Line resolveLine(Screen screen, ScrollbackBuffer scrollback, int row) {
        validateScreen(screen);
        validateScrollback(scrollback);

        List<Line> scrollbackLines = scrollback.getLines();
        int scrollbackSize = scrollbackLines.size();
        int totalHeight = scrollbackSize + screen.getHeight();

        if (row < 0 || row >= totalHeight) {
            throw new IndexOutOfBoundsException(
                    "row index out of bounds: " + row + ", total buffer height: " + totalHeight
            );
        }

        if (row < scrollbackSize) {
            return scrollbackLines.get(row);
        }

        return screen.getLine(row - scrollbackSize);
    }

    private void validateScreen(Screen screen) {
        if (screen == null) {
            throw new IllegalArgumentException("Screen must not be null");
        }
    }

    private void validateScrollback(ScrollbackBuffer scrollback) {
        if (scrollback == null) {
            throw new IllegalArgumentException("Scrollback buffer must not be null");
        }
    }
}
