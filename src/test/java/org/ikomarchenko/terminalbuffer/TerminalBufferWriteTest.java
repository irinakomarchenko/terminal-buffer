package org.ikomarchenko.terminalbuffer;

import org.ikomarchenko.terminalbuffer.model.TerminalColor;
import org.ikomarchenko.terminalbuffer.model.TextAttributes;
import org.ikomarchenko.terminalbuffer.model.TextStyle;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.EnumSet;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class TerminalBufferWriteTest {

    private TerminalBuffer buffer;

    @BeforeEach
    void setUp() {
        buffer = new TerminalBuffer(5, 3, 10);
    }

    @Test
    void shouldWriteSingleCharacterAtCurrentCursorPosition() {
        buffer.write("A");

        assertCharacterAt(0, 0, 'A');
        assertCursorPosition(1, 0);
    }

    @Test
    void shouldWriteSeveralCharactersInSameLine() {
        buffer.write("ABC");

        assertLineEquals(0, "ABC  ");
        assertCursorPosition(3, 0);
    }

    @Test
    void shouldWrapToNextLineWhenEndOfLineReached() {
        buffer.write("ABCDE");

        assertLineEquals(0, "ABCDE");
        assertCursorPosition(0, 1);
    }

    @Test
    void shouldContinueWritingOnNextLineAfterWrap() {
        buffer.write("ABCDEF");

        assertLineEquals(0, "ABCDE");
        assertLineEquals(1, "F    ");
        assertCursorPosition(1, 1);
    }

    @Test
    void shouldOverwriteExistingCharacterWhenWritingAtSpecificCursorPosition() {
        buffer.write("ABCDE");

        buffer.setCursor(2, 0);
        buffer.write("Z");

        assertLineEquals(0, "ABZDE");
        assertCursorPosition(3, 0);
    }

    @Test
    void shouldWriteUsingCurrentAttributes() {
        TextAttributes attributes = new TextAttributes(
                TerminalColor.GREEN,
                TerminalColor.BLACK,
                EnumSet.of(TextStyle.BOLD, TextStyle.UNDERLINE)
        );

        buffer.setCurrentAttributes(attributes);
        buffer.write("A");

        assertEquals(attributes, buffer.getAttributesAt(0, 0));
    }

    @Test
    void shouldPreserveAttributesForPreviouslyWrittenCellsAfterAttributesChange() {
        TextAttributes firstAttributes = new TextAttributes(
                TerminalColor.RED,
                TerminalColor.DEFAULT,
                EnumSet.of(TextStyle.BOLD)
        );

        TextAttributes secondAttributes = new TextAttributes(
                TerminalColor.BLUE,
                TerminalColor.DEFAULT,
                EnumSet.of(TextStyle.ITALIC)
        );

        buffer.setCurrentAttributes(firstAttributes);
        buffer.write("A");

        buffer.setCurrentAttributes(secondAttributes);
        buffer.write("B");

        assertEquals(firstAttributes, buffer.getAttributesAt(0, 0));
        assertEquals(secondAttributes, buffer.getAttributesAt(0, 1));
    }

    @Test
    void shouldFillCurrentLineWithCharacterUsingCurrentAttributes() {
        TextAttributes attributes = new TextAttributes(
                TerminalColor.YELLOW,
                TerminalColor.BLUE,
                EnumSet.of(TextStyle.BOLD)
        );

        buffer.setCurrentAttributes(attributes);
        buffer.fillLine('*');

        assertLineEquals(0, "*****");
        assertEquals(attributes, buffer.getAttributesAt(0, 0));
        assertEquals(attributes, buffer.getAttributesAt(0, 4));
    }

    @Test
    void shouldNotMoveCursorWhenFillingLine() {
        buffer.setCursor(2, 1);

        buffer.fillLine('-');

        assertLineEquals(1, "-----");
        assertCursorPosition(2, 1);
    }

    @Test
    void shouldThrowExceptionWhenWritingNullText() {
        assertThrows(IllegalArgumentException.class, () -> buffer.write(null));
    }

    @Test
    void shouldThrowExceptionWhenSettingNullAttributes() {
        assertThrows(IllegalArgumentException.class, () -> buffer.setCurrentAttributes(null));
    }

    @Test
    void shouldScrollScreenWhenWritingPastBottomRightCorner() {
        TerminalBuffer smallBuffer = new TerminalBuffer(3, 2, 10);

        smallBuffer.write("ABCDEF");

        assertEquals("DEF", smallBuffer.getScreen().getLine(0).asString());
        assertEquals("   ", smallBuffer.getScreen().getLine(1).asString());
        assertEquals(0, smallBuffer.getCursor().getColumn());
        assertEquals(1, smallBuffer.getCursor().getRow());
        assertEquals(1, smallBuffer.getScrollback().size());
        assertEquals("ABC", smallBuffer.getScrollback().getLines().get(0).asString());
    }

    @Test
    void shouldKeepOnlyLastScrollbackLinesUpToLimit() {
        TerminalBuffer limitedBuffer = new TerminalBuffer(3, 2, 2);

        limitedBuffer.write("ABCDEFGHIJKL");

        assertEquals(2, limitedBuffer.getScrollback().size());
        assertEquals("JKL", limitedBuffer.getScreen().getLine(0).asString());
        assertEquals("   ", limitedBuffer.getScreen().getLine(1).asString());
        assertEquals("DEF", limitedBuffer.getScrollback().getLines().get(0).asString());
        assertEquals("GHI", limitedBuffer.getScrollback().getLines().get(1).asString());
    }

    @Test
    void shouldNotStoreScrollbackWhenLimitIsZero() {
        TerminalBuffer noScrollbackBuffer = new TerminalBuffer(3, 2, 0);

        noScrollbackBuffer.write("ABCDEFGHI");

        assertEquals(0, noScrollbackBuffer.getScrollback().size());
        assertEquals("GHI", noScrollbackBuffer.getScreen().getLine(0).asString());
        assertEquals("   ", noScrollbackBuffer.getScreen().getLine(1).asString());
    }

    private void assertCursorPosition(int expectedColumn, int expectedRow) {
        assertEquals(expectedColumn, buffer.getCursor().getColumn());
        assertEquals(expectedRow, buffer.getCursor().getRow());
    }

    private void assertCharacterAt(int row, int column, char expectedCharacter) {
        assertEquals(expectedCharacter, buffer.getCharacterAt(row, column));
    }

    private void assertLineEquals(int row, String expectedLine) {
        assertEquals(expectedLine, buffer.getLineAsString(row));
    }
}
