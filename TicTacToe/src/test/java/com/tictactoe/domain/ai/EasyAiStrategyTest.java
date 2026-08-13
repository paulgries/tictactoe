package com.tictactoe.domain.ai;

import static org.assertj.core.api.Assertions.assertThat;

import com.tictactoe.domain.Board;
import com.tictactoe.domain.GameConfig;
import com.tictactoe.domain.Mark;
import com.tictactoe.domain.Position;
import org.junit.jupiter.api.Test;

class EasyAiStrategyTest {

    private static final GameConfig CONFIG_3X3 = new GameConfig(3, 3);
    private final EasyAiStrategy strategy = new EasyAiStrategy();

    @Test
    void selectsImmediateWinningMoveWhenAvailable() {
        Board board = new Board(3)
            .placeMark(new Position(0, 0), Mark.X)
            .placeMark(new Position(0, 1), Mark.X);

        Position move = strategy.selectMove(board, CONFIG_3X3, Mark.X);

        assertThat(move).isEqualTo(new Position(0, 2));
    }

    @Test
    void selectsBlockingMoveWhenNoWinButOpponentThreatens() {
        Board board = new Board(3)
            .placeMark(new Position(1, 0), Mark.O)
            .placeMark(new Position(1, 1), Mark.O)
            .placeMark(new Position(2, 2), Mark.X);

        Position move = strategy.selectMove(board, CONFIG_3X3, Mark.X);

        assertThat(move).isEqualTo(new Position(1, 2));
    }

    @Test
    void prefersWinOverBlockWhenBothAvailable() {
        Board board = new Board(3)
            .placeMark(new Position(0, 0), Mark.X)
            .placeMark(new Position(0, 1), Mark.X)
            .placeMark(new Position(1, 0), Mark.O)
            .placeMark(new Position(1, 1), Mark.O);

        Position move = strategy.selectMove(board, CONFIG_3X3, Mark.X);

        assertThat(move).isEqualTo(new Position(0, 2));
    }

    @Test
    void fallsBackToCenterOnEmptyOddSizedBoard() {
        Board board = new Board(3);

        Position move = strategy.selectMove(board, CONFIG_3X3, Mark.X);

        assertThat(move).isEqualTo(new Position(1, 1));
    }

    @Test
    void neverReturnsOccupiedOrOutOfBoundsPosition() {
        Board board = new Board(3)
            .placeMark(new Position(1, 0), Mark.O)
            .placeMark(new Position(1, 1), Mark.O)
            .placeMark(new Position(2, 2), Mark.X);

        Position move = strategy.selectMove(board, CONFIG_3X3, Mark.X);

        assertThat(board.emptyPositions()).contains(move);
    }
}
