package com.tictactoe.game.ai;

import static org.assertj.core.api.Assertions.assertThat;

import com.tictactoe.game.domain.Board;
import com.tictactoe.game.domain.GameConfig;
import com.tictactoe.game.domain.Mark;
import com.tictactoe.game.domain.Position;
import com.tictactoe.game.testutil.GameFixtures;
import org.junit.jupiter.api.Test;

class BoardEvaluatorTest {

    private static final GameConfig CONFIG_3X3 = new GameConfig(3, 3);

    @Test
    void scoresWinForMaximizingPlayerPositively() {
        Board board = GameFixtures.wonByXBoard();

        long score = BoardEvaluator.score(board, CONFIG_3X3, Mark.X);

        assertThat(score).isPositive();
    }

    @Test
    void scoresWinForOpponentNegatively() {
        Board board = GameFixtures.wonByOBoard();

        long score = BoardEvaluator.score(board, CONFIG_3X3, Mark.X);

        assertThat(score).isNegative();
    }

    @Test
    void scoresDrawAsZero() {
        Board board = GameFixtures.drawnBoard();

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
