package org.ikomarchenko.terminalbuffer;

import org.ikomarchenko.terminalbuffer.model.Cursor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class TerminalBufferCursorTest {

    private static final int WIDTH = 5;
    private static final int HEIGHT = 3;
    private static final int SCROLLBACK_LIMIT = 10;

    private TerminalBuffer buffer;

    @BeforeEach
    void setUp() {
        buffer = new TerminalBuffer(WIDTH, HEIGHT, SCROLLBACK_LIMIT);
    }

    @Test
    void shouldStartWithCursorAtOrigin() {
        assertCursorPosition(0, 0);
    }

    @Test
    void shouldSetCursorWithinBounds() {
        buffer.setCursor(2, 1);

        assertCursorPosition(2, 1);
    }

    @Test
    void shouldClampCursorColumnWhenTooLarge() {
        buffer.setCursor(100, 1);

        assertCursorPosition(4, 1);
    }

    @Test
    void shouldClampCursorRowWhenTooLarge() {
        buffer.setCursor(1, 100);

        assertCursorPosition(1, 2);
    }

    @Test
    void shouldClampCursorWhenNegative() {
        buffer.setCursor(-10, -10);

        assertCursorPosition(0, 0);
    }

    @Test
    void shouldMoveCursorRight() {
        buffer.moveCursorRight(2);

        assertCursorPosition(2, 0);
    }

    @Test
    void shouldClampCursorWhenMovingRightPastEdge() {
        buffer.moveCursorRight(10);

        assertCursorPosition(4, 0);
    }

    @Test
    void shouldMoveCursorLeft() {
        buffer.setCursor(3, 0);
        buffer.moveCursorLeft(2);

        assertCursorPosition(1, 0);
    }

    @Test
    void shouldClampCursorWhenMovingLeftPastZero() {
        buffer.setCursor(1, 0);
        buffer.moveCursorLeft(10);

        assertCursorPosition(0, 0);
    }

    @Test
    void shouldMoveCursorDown() {
        buffer.moveCursorDown(2);

        assertCursorPosition(0, 2);
    }

    @Test
    void shouldClampCursorWhenMovingDownPastBottom() {
        buffer.moveCursorDown(10);

        assertCursorPosition(0, 2);
    }

    @Test
    void shouldMoveCursorUp() {
        buffer.setCursor(0, 2);
        buffer.moveCursorUp(1);

        assertCursorPosition(0, 1);
    }

    @Test
    void shouldClampCursorWhenMovingUpPastTop() {
        buffer.setCursor(0, 1);
        buffer.moveCursorUp(10);

        assertCursorPosition(0, 0);
    }

    @Test
    void shouldThrowWhenMovementNegative() {
        assertThrows(IllegalArgumentException.class, () -> buffer.moveCursorRight(-1));
    }

    private void assertCursorPosition(int expectedColumn, int expectedRow) {
        Cursor cursor = buffer.getCursor();
        assertEquals(expectedColumn, cursor.getColumn());
        assertEquals(expectedRow, cursor.getRow());
    }
}
