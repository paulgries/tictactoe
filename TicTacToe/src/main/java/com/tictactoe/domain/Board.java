package com.tictactoe.domain;

import com.tictactoe.domain.exception.InvalidMoveException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public final class Board {

    private final int size;
    private final Mark[][] cells;

    public Board(int size) {
        this.size = size;
        this.cells = new Mark[size][size];
    }

    private Board(int size, Mark[][] cells) {
        this.size = size;
        this.cells = cells;
    }

    public int size() {
        return size;
    }

    public Optional<Mark> get(Position position) {
        requireInBounds(position);
        return Optional.ofNullable(cells[position.row()][position.column()]);
    }

    public Board placeMark(Position position, Mark mark) {
        requireInBounds(position);
        if (cells[position.row()][position.column()] != null) {
            throw new InvalidMoveException("cell already occupied: " + position);
        }

        Mark[][] copy = new Mark[size][];
        for (int row = 0; row < size; row++) {
            copy[row] = cells[row].clone();
        }
        copy[position.row()][position.column()] = mark;
        return new Board(size, copy);
    }

    public boolean isFull() {
        for (Mark[] row : cells) {
            for (Mark cell : row) {
                if (cell == null) {
                    return false;
                }
            }
        }
        return true;
    }

    public List<Position> emptyPositions() {
        List<Position> positions = new ArrayList<>();
        for (int row = 0; row < size; row++) {
            for (int column = 0; column < size; column++) {
                if (cells[row][column] == null) {
                    positions.add(new Position(row, column));
                }
            }
        }
        return positions;
    }

    private void requireInBounds(Position position) {
        if (position.row() >= size || position.column() >= size) {
            throw new InvalidMoveException("position out of bounds: " + position);
        }
    }
}
