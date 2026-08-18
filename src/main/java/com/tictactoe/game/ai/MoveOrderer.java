package com.tictactoe.domain.ai;

import com.tictactoe.domain.Board;
import com.tictactoe.domain.Position;
import java.util.ArrayList;
import java.util.List;

/**
 * Orders candidate moves to make alpha-beta pruning more effective: cells adjacent to
 * existing marks first, then the board center, then everything else.
 */
final class MoveOrderer {

    private MoveOrderer() {
    }

    static List<Position> order(Board board, List<Position> emptyPositions) {
        int size = board.size();
        boolean hasCenter = size % 2 == 1;
        Position center = hasCenter ? new Position(size / 2, size / 2) : null;

        List<Position> adjacent = new ArrayList<>();
        List<Position> centerList = new ArrayList<>();
        List<Position> rest = new ArrayList<>();
        for (Position position : emptyPositions) {
            if (position.equals(center)) {
                centerList.add(position);
            } else if (isAdjacentToExistingMark(board, position)) {
                adjacent.add(position);
            } else {
                rest.add(position);
            }
        }

        List<Position> ordered = new ArrayList<>(emptyPositions.size());
        ordered.addAll(adjacent);
        ordered.addAll(centerList);
        ordered.addAll(rest);
        return ordered;
    }

    private static boolean isAdjacentToExistingMark(Board board, Position position) {
        int size = board.size();
        for (int rowOffset = -1; rowOffset <= 1; rowOffset++) {
            for (int columnOffset = -1; columnOffset <= 1; columnOffset++) {
                if (rowOffset == 0 && columnOffset == 0) {
                    continue;
                }
                int row = position.row() + rowOffset;
                int column = position.column() + columnOffset;
                if (row >= 0 && row < size && column >= 0 && column < size
                        && board.get(new Position(row, column)).isPresent()) {
                    return true;
                }
            }
        }
        return false;
    }
}
