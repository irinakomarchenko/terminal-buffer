package org.ikomarchenko.terminalbuffer;

import org.ikomarchenko.terminalbuffer.model.TerminalColor;
import org.ikomarchenko.terminalbuffer.model.TextAttributes;
import org.ikomarchenko.terminalbuffer.model.TextStyle;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.EnumSet;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class TerminalBufferInsertTest {

    private TerminalBuffer buffer;

    @BeforeEach
    void setUp() {
        buffer = new TerminalBuffer(5, 3, 10);
    }

    @Test
    void shouldInsertSingleCharacterIntoEmptyLine() {
        buffer.insert("A");

        assertLineEquals(0, "A    ");
        assertCursorPosition(1, 0);
    }

    @Test
    void shouldInsertCharacterAtCursorAndShiftLineRight() {
        buffer.write("ABCD");
        buffer.setCursor(1, 0);

        buffer.insert("X");

        assertLineEquals(0, "AXBCD");
        assertCursorPosition(2, 0);
    }

    @Test
    void shouldInsertSeveralCharactersAtCursor() {
        buffer.write("ABC");
        buffer.setCursor(1, 0);

        buffer.insert("XY");

        assertLineEquals(0, "AXYBC");
        assertCursorPosition(3, 0);
    }

    @Test
    void shouldPushOverflowToNextLineWhenCurrentLineIsFull() {
        buffer.write("ABCDE");
        buffer.setCursor(2, 0);

        buffer.insert("X");

        assertLineEquals(0, "ABXCD");
        assertLineEquals(1, "E    ");
        assertCursorPosition(3, 0);
    }

    @Test
    void shouldPropagateOverflowAcrossMultipleLines() {
        TerminalBuffer smallBuffer = new TerminalBuffer(3, 3, 10);

        smallBuffer.write("abcdefghi");

        assertEquals(1, smallBuffer.getScrollback().size());
        assertEquals("abc", smallBuffer.getScrollback().getLines().get(0).asString());
        assertEquals("def", smallBuffer.getScreen().getLine(0).asString());
        assertEquals("ghi", smallBuffer.getScreen().getLine(1).asString());
        assertEquals("   ", smallBuffer.getScreen().getLine(2).asString());

        smallBuffer.setCursor(1, 0);
        smallBuffer.insert("X");

        assertEquals("dXe", smallBuffer.getScreen().getLine(0).asString());
        assertEquals("fgh", smallBuffer.getScreen().getLine(1).asString());
        assertEquals("i  ", smallBuffer.getScreen().getLine(2).asString());

        assertEquals(1, smallBuffer.getScrollback().size());
        assertEquals("abc", smallBuffer.getScrollback().getLines().get(0).asString());

        assertEquals(2, smallBuffer.getCursor().getColumn());
        assertEquals(0, smallBuffer.getCursor().getRow());
    }

    @Test
    void shouldPropagateOverflowToFollowingLinesWithoutAdditionalScroll() {
        TerminalBuffer smallBuffer = new TerminalBuffer(3, 2, 10);

        smallBuffer.write("abcdef");

        assertEquals(1, smallBuffer.getScrollback().size());
        assertEquals("abc", smallBuffer.getScrollback().getLines().get(0).asString());
        assertEquals("def", smallBuffer.getScreen().getLine(0).asString());
        assertEquals("   ", smallBuffer.getScreen().getLine(1).asString());

        smallBuffer.setCursor(1, 0);
        smallBuffer.insert("X");

        assertEquals("dXe", smallBuffer.getScreen().getLine(0).asString());
        assertEquals("f  ", smallBuffer.getScreen().getLine(1).asString());

        assertEquals(1, smallBuffer.getScrollback().size());
        assertEquals("abc", smallBuffer.getScrollback().getLines().get(0).asString());

        assertEquals(2, smallBuffer.getCursor().getColumn());
        assertEquals(0, smallBuffer.getCursor().getRow());
    }

    @Test
    void shouldKeepOnlyLastScrollbackLinesUpToLimitDuringInsert() {
        TerminalBuffer limitedBuffer = new TerminalBuffer(3, 2, 1);

        limitedBuffer.write("abcdef");

        assertEquals(1, limitedBuffer.getScrollback().size());
        assertEquals("abc", limitedBuffer.getScrollback().getLines().get(0).asString());
        assertEquals("def", limitedBuffer.getScreen().getLine(0).asString());
        assertEquals("   ", limitedBuffer.getScreen().getLine(1).asString());

        limitedBuffer.setCursor(1, 0);
        limitedBuffer.insert("XYZ");

        assertEquals(1, limitedBuffer.getScrollback().size());
        assertEquals("abc", limitedBuffer.getScrollback().getLines().get(0).asString());
        assertEquals("dXY", limitedBuffer.getScreen().getLine(0).asString());
        assertEquals("Zef", limitedBuffer.getScreen().getLine(1).asString());
    }

    @Test
    void shouldInsertUsingCurrentAttributes() {
        TextAttributes attributes = new TextAttributes(
                TerminalColor.GREEN,
                TerminalColor.BLACK,
                EnumSet.of(TextStyle.BOLD)
        );

        buffer.setCurrentAttributes(attributes);

        buffer.insert("A");

        assertEquals(attributes, buffer.getAttributesAt(0, 0));
    }

    @Test
    void shouldPreserveAttributesOfExistingCellsAfterInsert() {
        TextAttributes originalAttributes = new TextAttributes(
                TerminalColor.RED,
                TerminalColor.DEFAULT,
                EnumSet.of(TextStyle.UNDERLINE)
        );
        TextAttributes insertedAttributes = new TextAttributes(
                TerminalColor.BLUE,
                TerminalColor.DEFAULT,
                EnumSet.of(TextStyle.ITALIC)
        );

        buffer.setCurrentAttributes(originalAttributes);
        buffer.write("AB");

        buffer.setCursor(1, 0);
        buffer.setCurrentAttributes(insertedAttributes);
        buffer.insert("X");

        assertLineEquals(0, "AXB  ");
        assertEquals(originalAttributes, buffer.getAttributesAt(0, 0));
        assertEquals(insertedAttributes, buffer.getAttributesAt(0, 1));
        assertEquals(originalAttributes, buffer.getAttributesAt(0, 2));
    }

    @Test
    void shouldMoveCursorToNextLineWhenInsertAtLastColumn() {
        buffer.write("ABCD");
        buffer.setCursor(4, 0);

        buffer.insert("X");

        assertLineEquals(0, "ABCDX");
        assertCursorPosition(0, 1);
    }

    @Test
    void shouldKeepCursorOnLastRowWhenInsertAtBottomRightCorner() {
        TerminalBuffer smallBuffer = new TerminalBuffer(3, 2, 10);

        smallBuffer.write("abcde");
        smallBuffer.setCursor(2, 1);

        smallBuffer.insert("X");

        assertEquals(0, smallBuffer.getCursor().getColumn());
        assertEquals(1, smallBuffer.getCursor().getRow());
    }

    @Test
    void shouldThrowExceptionWhenInsertingNullText() {
        assertThrows(IllegalArgumentException.class, () -> buffer.insert(null));
    }

    private void assertCursorPosition(int expectedColumn, int expectedRow) {
        assertEquals(expectedColumn, buffer.getCursor().getColumn());
        assertEquals(expectedRow, buffer.getCursor().getRow());
    }

    private void assertLineEquals(int row, String expectedLine) {
        assertEquals(expectedLine, buffer.getLineAsString(row));
    }
}

