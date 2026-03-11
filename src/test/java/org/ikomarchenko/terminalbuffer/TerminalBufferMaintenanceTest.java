package org.ikomarchenko.terminalbuffer;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TerminalBufferMaintenanceTest {

    private TerminalBuffer buffer;

    @BeforeEach
    void setUp() {
        buffer = new TerminalBuffer(3, 2, 10);
    }

    @Test
    void shouldInsertEmptyLineAtBottomAndMoveTopLineToScrollback() {
        buffer.write("abc");

        buffer.insertEmptyLineAtBottom();

        assertScreenLineEquals(0, "   ");
        assertScreenLineEquals(1, "   ");
        assertScrollbackSize(1);
        assertScrollbackLineEquals(0, "abc");
    }

    @Test
    void shouldPreserveExistingScrollbackWhenInsertingEmptyLineAtBottom() {
        buffer.write("abcdef");

        assertScrollbackSize(1);
        assertScrollbackLineEquals(0, "abc");
        assertScreenLineEquals(0, "def");
        assertScreenLineEquals(1, "   ");

        buffer.insertEmptyLineAtBottom();

        assertScrollbackSize(2);
        assertScrollbackLineEquals(0, "abc");
        assertScrollbackLineEquals(1, "def");
        assertScreenLineEquals(0, "   ");
        assertScreenLineEquals(1, "   ");
    }

    @Test
    void shouldRespectScrollbackLimitWhenInsertingEmptyLineAtBottom() {
        TerminalBuffer limitedBuffer = new TerminalBuffer(3, 2, 1);

        limitedBuffer.write("abcdef");
        limitedBuffer.insertEmptyLineAtBottom();

        assertEquals(1, limitedBuffer.getScrollback().size());
        assertEquals("def", limitedBuffer.getScrollback().getLines().get(0).asString());
        assertEquals("   ", limitedBuffer.getScreen().getLine(0).asString());
        assertEquals("   ", limitedBuffer.getScreen().getLine(1).asString());
    }

    @Test
    void shouldClearScreenOnly() {
        buffer.write("abc");

        buffer.clearScreen();

        assertScreenLineEquals(0, "   ");
        assertScreenLineEquals(1, "   ");
        assertScrollbackSize(0);
    }

    @Test
    void shouldNotClearScrollbackWhenClearingScreen() {
        buffer.write("abcdef");

        buffer.clearScreen();

        assertScrollbackSize(1);
        assertScrollbackLineEquals(0, "abc");
        assertScreenLineEquals(0, "   ");
        assertScreenLineEquals(1, "   ");
    }

    @Test
    void shouldNotResetCursorWhenClearingScreen() {
        buffer.setCursor(2, 1);

        buffer.clearScreen();

        assertCursorPosition(2, 1);
    }

    @Test
    void shouldClearScreenAndScrollback() {
        buffer.write("abcdef");

        buffer.clearScreenAndScrollback();

        assertScrollbackSize(0);
        assertScreenLineEquals(0, "   ");
        assertScreenLineEquals(1, "   ");
    }

    @Test
    void shouldNotResetCursorWhenClearingScreenAndScrollback() {
        buffer.write("abcdef");
        buffer.setCursor(1, 1);

        buffer.clearScreenAndScrollback();

        assertCursorPosition(1, 1);
        assertScrollbackSize(0);
        assertScreenLineEquals(0, "   ");
        assertScreenLineEquals(1, "   ");
    }

    private void assertCursorPosition(int expectedColumn, int expectedRow) {
        assertEquals(expectedColumn, buffer.getCursor().getColumn());
        assertEquals(expectedRow, buffer.getCursor().getRow());
    }

    private void assertScreenLineEquals(int row, String expectedLine) {
        assertEquals(expectedLine, buffer.getScreen().getLine(row).asString());
    }

    private void assertScrollbackLineEquals(int index, String expectedLine) {
        assertEquals(expectedLine, buffer.getScrollback().getLines().get(index).asString());
    }

    private void assertScrollbackSize(int expectedSize) {
        assertEquals(expectedSize, buffer.getScrollback().size());
    }
}
