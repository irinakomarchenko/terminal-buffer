package org.ikomarchenko.terminalbuffer.model;

import java.util.Objects;

public final class Cell {

    public static final char EMPTY_CHARACTER = ' ';

    private final char character;
    private final TextAttributes attributes;

    public Cell(char character, TextAttributes attributes) {
        this.character = character;
        this.attributes = Objects.requireNonNull(attributes, "attributes must not be null");
    }

    public static Cell empty() {
        return new Cell(EMPTY_CHARACTER, TextAttributes.defaultAttributes());
    }

    public char getCharacter() {
        return character;
    }

    public TextAttributes getAttributes() {
        return attributes;
    }

    public boolean isEmpty() {
        return character == EMPTY_CHARACTER;
    }

    @Override
    public String toString() {
        return "Cell{" +
                "character=" + character +
                ", attributes=" + attributes +
                '}';
    }
}
