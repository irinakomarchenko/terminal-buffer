package org.ikomarchenko.terminalbuffer;

import org.ikomarchenko.terminalbuffer.model.TerminalColor;
import org.ikomarchenko.terminalbuffer.model.TextAttributes;
import org.ikomarchenko.terminalbuffer.model.TextStyle;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.EnumSet;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class TerminalBufferContentTest {

    private TerminalBuffer buffer;

    @BeforeEach
    void setUp() {
        buffer = new TerminalBuffer(4, 2, 10);
    }

    @Test
    void shouldReturnCharacterAtScreenPositionWhenScrollbackIsEmpty() {
        buffer.write("AB");

        assertEquals('A', buffer.getCharacterAt(0, 0));
        assertEquals('B', buffer.getCharacterAt(0, 1));
        assertEquals(' ', buffer.getCharacterAt(0, 2));
    }

    @Test
    void shouldReturnCharacterAtLogicalBufferPositionWhenScrollbackExists() {
        buffer.write("ABCDEFGH");

        assertEquals(1, buffer.getScrollback().size());
        assertEquals('A', buffer.getCharacterAt(0, 0));
        assertEquals('D', buffer.getCharacterAt(0, 3));
        assertEquals('E', buffer.getCharacterAt(1, 0));
        assertEquals('H', buffer.getCharacterAt(1, 3));
        assertEquals(' ', buffer.getCharacterAt(2, 0));
    }

    @Test
    void shouldReturnAttributesAtLogicalBufferPosition() {
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
    void shouldReturnDefaultAttributesForEmptyCell() {
        buffer.write("A");

        assertEquals(TextAttributes.defaultAttributes(), buffer.getAttributesAt(0, 1));
    }

    @Test
    void shouldReturnLineAsStringFromScreenWhenScrollbackIsEmpty() {
        buffer.write("AB");

        assertEquals("AB  ", buffer.getLineAsString(0));
        assertEquals("    ", buffer.getLineAsString(1));
    }

    @Test
    void shouldReturnLineAsStringFromScrollbackAndScreenInLogicalOrder() {
        buffer.write("ABCDEFGH");

        assertEquals("ABCD", buffer.getLineAsString(0));
        assertEquals("EFGH", buffer.getLineAsString(1));
        assertEquals("    ", buffer.getLineAsString(2));
    }

    @Test
    void shouldReturnScreenContentAsString() {
        buffer.write("ABCDEF");

        String expected = String.join(System.lineSeparator(),
                "ABCD",
                "EF  "
        );

        assertEquals(expected, buffer.getScreenContentAsString());
    }

    @Test
    void shouldReturnBufferContentAsStringIncludingScrollback() {
        buffer.write("ABCDEFGH");

        String expected = String.join(System.lineSeparator(),
                "ABCD",
                "EFGH",
                "    "
        );

        assertEquals(expected, buffer.getBufferContentAsString());
    }

    @Test
    void shouldReturnOnlyScreenContentWhenScrollbackIsEmpty() {
        buffer.write("AB");

        String expected = String.join(System.lineSeparator(),
                "AB  ",
                "    "
        );

        assertEquals(expected, buffer.getBufferContentAsString());
    }

    @Test
    void shouldThrowWhenCharacterRowIsNegative() {
        assertThrows(IndexOutOfBoundsException.class, () -> buffer.getCharacterAt(-1, 0));
    }

    @Test
    void shouldThrowWhenCharacterRowExceedsLogicalBufferHeight() {
        buffer.write("AB");

        assertThrows(IndexOutOfBoundsException.class, () -> buffer.getCharacterAt(2, 0));
    }

    @Test
    void shouldThrowWhenCharacterColumnIsNegative() {
        assertThrows(IndexOutOfBoundsException.class, () -> buffer.getCharacterAt(0, -1));
    }

    @Test
    void shouldThrowWhenCharacterColumnExceedsWidth() {
        assertThrows(IndexOutOfBoundsException.class, () -> buffer.getCharacterAt(0, 4));
    }

    @Test
    void shouldThrowWhenGettingAttributesForInvalidRow() {
        assertThrows(IndexOutOfBoundsException.class, () -> buffer.getAttributesAt(5, 0));
    }

    @Test
    void shouldThrowWhenGettingLineForInvalidRow() {
        assertThrows(IndexOutOfBoundsException.class, () -> buffer.getLineAsString(2));
    }
}
