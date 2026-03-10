package org.ikomarchenko.terminalbuffer.service;

import org.ikomarchenko.terminalbuffer.model.Cursor;

public final class CursorService {

    public void setCursor(Cursor cursor, int column, int row, int width, int height) {
        int clampedColumn = clamp(column, 0, width - 1);
        int clampedRow = clamp(row, 0, height - 1);
        cursor.setPosition(clampedColumn, clampedRow);
    }

    public void moveUp(Cursor cursor, int cells, int width, int height) {
        validateMovement(cells);
        setCursor(cursor, cursor.getColumn(), cursor.getRow() - cells, width, height);
    }

    public void moveDown(Cursor cursor, int cells, int width, int height) {
        validateMovement(cells);
        setCursor(cursor, cursor.getColumn(), cursor.getRow() + cells, width, height);
    }

    public void moveLeft(Cursor cursor, int cells, int width, int height) {
        validateMovement(cells);
        setCursor(cursor, cursor.getColumn() - cells, cursor.getRow(), width, height);
    }

    public void moveRight(Cursor cursor, int cells, int width, int height) {
        validateMovement(cells);
        setCursor(cursor, cursor.getColumn() + cells, cursor.getRow(), width, height);
    }

    private void validateMovement(int cells) {
        if (cells < 0) {
            throw new IllegalArgumentException("Movement must not be negative");
        }
    }

    private int clamp(int value, int min, int max) {
        return Math.max(min, Math.min(value, max));
    }
}
