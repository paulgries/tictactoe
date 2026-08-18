package com.tictactoe.game.ai;

import static org.assertj.core.api.Assertions.assertThat;

import com.tictactoe.game.domain.Board;
import com.tictactoe.game.domain.Mark;
import com.tictactoe.game.domain.Position;
import java.util.List;
import org.junit.jupiter.api.Test;

class MoveOrdererTest {

    @Test
    void orderIsAPermutationOfTheGivenEmptyPositions() {
        Board board = new Board(3).placeMark(new Position(0, 0), Mark.X);
        List<Position> emptyPositions = board.emptyPositions();

        List<Position> ordered = MoveOrderer.order(board, emptyPositions);

        assertThat(ordered).containsExactlyInAnyOrderElementsOf(emptyPositions);
    }

    @Test
    void centerIsOrderedFirstOnAnEmptyOddSizedBoard() {
        Board board = new Board(3);

        List<Position> ordered = MoveOrderer.order(board, board.emptyPositions());

        assertThat(ordered.get(0)).isEqualTo(new Position(1, 1));
    }

    @Test
    void positionsAdjacentToAnExistingMarkComeBeforeDistantNonCenterPositions() {
        Board board = new Board(5).placeMark(new Position(0, 0), Mark.X);

        List<Position> ordered = MoveOrderer.order(board, board.emptyPositions());

        int adjacentIndex = ordered.indexOf(new Position(0, 1));
        int distantIndex = ordered.indexOf(new Position(4, 4));

        assertThat(adjacentIndex).isLessThan(distantIndex);
    }
}
