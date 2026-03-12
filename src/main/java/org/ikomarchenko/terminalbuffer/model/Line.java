package org.ikomarchenko.terminalbuffer.model;

import java.util.Arrays;

public final class Line {

    private final Cell[] cells;

    public Line(int width) {
        if (width <= 0) {
            throw new IllegalArgumentException("Width must be greater than 0");
        }
        this.cells = new Cell[width];
        Arrays.fill(this.cells, Cell.empty());
    }

    public int length() {
        return cells.length;
    }

    public Cell getCell(int column) {
        checkColumnIndex(column);
        return cells[column];
    }

    public void setCell(int column, Cell cell) {
        checkColumnIndex(column);

        if (cell == null) {
            throw new IllegalArgumentException("Cell must not be null");
        }

        cells[column] = cell;
    }

    public void fill(Cell cell) {
        if (cell == null) {
            throw new IllegalArgumentException("Cell must not be null");
        }
        Arrays.fill(cells, cell);
    }

    public  Cell[] copyCells() {
        return Arrays.copyOf(cells, cells.length);
    }

    public String asString() {
        StringBuilder builder = new StringBuilder(cells.length);
        for (Cell cell : cells) {
            builder.append(cell.getCharacter());
        }
        return builder.toString();
    }

        private void checkColumnIndex(int column) {
        if (column < 0 || column >= cells.length) {
            throw new IndexOutOfBoundsException(
                    "column index out of bounds: " + column + ", line width: " + cells.length
            );
        }
    }
}
