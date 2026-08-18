package com.tictactoe.domain.ai;

import static org.assertj.core.api.Assertions.assertThat;

import com.tictactoe.domain.Board;
import com.tictactoe.domain.GameConfig;
import com.tictactoe.domain.Mark;
import com.tictactoe.domain.Position;
import org.junit.jupiter.api.Test;

class BoardEvaluatorTest {

    private static final GameConfig CONFIG_3X3 = new GameConfig(3, 3);

    @Test
    void scoresWinForMaximizingPlayerPositively() {
        Board board = new Board(3)
            .placeMark(new Position(0, 0), Mark.X)
            .placeMark(new Position(0, 1), Mark.X)
            .placeMark(new Position(0, 2), Mark.X);

        long score = BoardEvaluator.score(board, CONFIG_3X3, Mark.X);

        assertThat(score).isPositive();
    }

    @Test
    void scoresWinForOpponentNegatively() {
        Board board = new Board(3)
            .placeMark(new Position(0, 0), Mark.O)
            .placeMark(new Position(0, 1), Mark.O)
            .placeMark(new Position(0, 2), Mark.O);

        long score = BoardEvaluator.score(board, CONFIG_3X3, Mark.X);

        assertThat(score).isNegative();
    }

    @Test
    void scoresDrawAsZero() {
        // X O X
        // X X O
        // O X O
        // every row, column and diagonal is contested (mixed marks)
        Board board = new Board(3)
            .placeMark(new Position(0, 0), Mark.X)
            .placeMark(new Position(0, 1), Mark.O)
            .placeMark(new Position(0, 2), Mark.X)
            .placeMark(new Position(1, 0), Mark.X)
            .placeMark(new Position(1, 1), Mark.X)
            .placeMark(new Position(1, 2), Mark.O)
            .placeMark(new Position(2, 0), Mark.O)
            .placeMark(new Position(2, 1), Mark.X)
            .placeMark(new Position(2, 2), Mark.O);

        long score = BoardEvaluator.score(board, CONFIG_3X3, Mark.X);

        assertThat(score).isZero();
    }

    @Test
    void scoresTwoInARowOpenHigherThanOneMarkOnly() {
        Board twoInARow = new Board(3)
            .placeMark(new Position(0, 0), Mark.X)
            .placeMark(new Position(0, 1), Mark.X);
        Board oneMarkOnly = new Board(3)
            .placeMark(new Position(0, 0), Mark.X);

        long twoInARowScore = BoardEvaluator.score(twoInARow, CONFIG_3X3, Mark.X);
        long oneMarkOnlyScore = BoardEvaluator.score(oneMarkOnly, CONFIG_3X3, Mark.X);

        assertThat(twoInARowScore).isGreaterThan(oneMarkOnlyScore);
    }
}
