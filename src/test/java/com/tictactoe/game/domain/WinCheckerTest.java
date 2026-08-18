package com.tictactoe.game.domain;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;

class WinCheckerTest {

    private static final GameConfig CONFIG_3X3 = new GameConfig(3, 3);
    private static final GameConfig CONFIG_5X5_WIN3 = new GameConfig(5, 3);

    @Test
    void evaluateReturnsInProgressOnEmptyBoard() {
        Board board = new Board(3);

        assertThat(WinChecker.evaluate(board, CONFIG_3X3)).isInstanceOf(InProgress.class);
    }

    @Test
    void evaluateDetectsHorizontalWin_3x3() {
        Board board = new Board(3)
            .placeMark(new Position(0, 0), Mark.X)
            .placeMark(new Position(0, 1), Mark.X)
            .placeMark(new Position(0, 2), Mark.X)
            .placeMark(new Position(1, 0), Mark.O)
            .placeMark(new Position(1, 1), Mark.O);

        GameStatus status = WinChecker.evaluate(board, CONFIG_3X3);

        assertThat(status).isInstanceOf(Win.class);
        Win win = (Win) status;
        assertThat(win.winner()).isEqualTo(Mark.X);
        assertThat(win.winningLine()).containsExactlyInAnyOrder(
            new Position(0, 0), new Position(0, 1), new Position(0, 2));
    }

    @Test
    void evaluateDetectsVerticalWin_3x3() {
        Board board = new Board(3)
            .placeMark(new Position(0, 0), Mark.X)
            .placeMark(new Position(1, 0), Mark.X)
            .placeMark(new Position(2, 0), Mark.X);

        GameStatus status = WinChecker.evaluate(board, CONFIG_3X3);

        assertThat(status).isInstanceOf(Win.class);
        Win win = (Win) status;
        assertThat(win.winner()).isEqualTo(Mark.X);
        assertThat(win.winningLine()).containsExactlyInAnyOrder(
            new Position(0, 0), new Position(1, 0), new Position(2, 0));
    }

    @Test
    void evaluateDetectsDiagonalWinTopLeftToBottomRight_3x3() {
        Board board = new Board(3)
            .placeMark(new Position(0, 0), Mark.O)
            .placeMark(new Position(1, 1), Mark.O)
            .placeMark(new Position(2, 2), Mark.O);

        GameStatus status = WinChecker.evaluate(board, CONFIG_3X3);

        assertThat(status).isInstanceOf(Win.class);
        Win win = (Win) status;
        assertThat(win.winner()).isEqualTo(Mark.O);
        assertThat(win.winningLine()).containsExactlyInAnyOrder(
            new Position(0, 0), new Position(1, 1), new Position(2, 2));
    }

    @Test
    void evaluateDetectsDiagonalWinTopRightToBottomLeft_3x3() {
        Board board = new Board(3)
            .placeMark(new Position(0, 2), Mark.O)
            .placeMark(new Position(1, 1), Mark.O)
            .placeMark(new Position(2, 0), Mark.O);

        GameStatus status = WinChecker.evaluate(board, CONFIG_3X3);

        assertThat(status).isInstanceOf(Win.class);
        Win win = (Win) status;
        assertThat(win.winner()).isEqualTo(Mark.O);
        assertThat(win.winningLine()).containsExactlyInAnyOrder(
            new Position(0, 2), new Position(1, 1), new Position(2, 0));
    }

    @Test
    void evaluateReturnsDrawWhenBoardFullWithNoWinner() {
        // X O X
        // X X O
        // O X O
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

        assertThat(WinChecker.evaluate(board, CONFIG_3X3)).isInstanceOf(Draw.class);
    }

    @Test
    void evaluateDetectsExactLengthWinOnLargerBoard_5x5_winLength3() {
        Board board = new Board(5)
            .placeMark(new Position(2, 1), Mark.X)
            .placeMark(new Position(2, 2), Mark.X)
            .placeMark(new Position(2, 3), Mark.X);

        GameStatus status = WinChecker.evaluate(board, CONFIG_5X5_WIN3);

        assertThat(status).isInstanceOf(Win.class);
        assertThat(((Win) status).winningLine()).containsExactlyInAnyOrder(
            new Position(2, 1), new Position(2, 2), new Position(2, 3));
    }

    @Test
    void evaluateDoesNotFalsePositiveOnGapInLine_5x5_winLength3() {
        Board board = new Board(5)
            .placeMark(new Position(2, 0), Mark.X)
            .placeMark(new Position(2, 2), Mark.X)
            .placeMark(new Position(2, 3), Mark.X);

        assertThat(WinChecker.evaluate(board, CONFIG_5X5_WIN3)).isInstanceOf(InProgress.class);
    }

    @Test
    void evaluateDetectsWinStartingFromCorner_5x5_winLength3() {
        Board board = new Board(5)
            .placeMark(new Position(0, 0), Mark.X)
            .placeMark(new Position(0, 1), Mark.X)
            .placeMark(new Position(0, 2), Mark.X);

        assertThat(WinChecker.evaluate(board, CONFIG_5X5_WIN3)).isInstanceOf(Win.class);
    }

    @Test
    void evaluateDetectsWinStartingFromEdgeNonCorner_5x5_winLength3() {
        Board board = new Board(5)
            .placeMark(new Position(0, 2), Mark.X)
            .placeMark(new Position(1, 2), Mark.X)
            .placeMark(new Position(2, 2), Mark.X);

        assertThat(WinChecker.evaluate(board, CONFIG_5X5_WIN3)).isInstanceOf(Win.class);
    }

    @Test
    void evaluateDetectsWinLongerThanWinLengthStillCounts_5x5_winLength3() {
        Board board = new Board(5)
            .placeMark(new Position(0, 0), Mark.X)
            .placeMark(new Position(0, 1), Mark.X)
            .placeMark(new Position(0, 2), Mark.X)
            .placeMark(new Position(0, 3), Mark.X);

        GameStatus status = WinChecker.evaluate(board, CONFIG_5X5_WIN3);

        assertThat(status).isInstanceOf(Win.class);
        assertThat(((Win) status).winningLine()).hasSize(3);
    }

    @Test
    void findWinningLineThroughReturnsEmptyWhenNoWinAtGivenPosition() {
        Board board = new Board(3).placeMark(new Position(1, 1), Mark.X);

        Optional<List<Position>> line =
            WinChecker.findWinningLineThrough(board, CONFIG_3X3, new Position(1, 1));

        assertThat(line).isEmpty();
    }

    @Test
    void findWinningLineThroughReturnsSegmentOfExactWinLength() {
        Board board = new Board(3)
            .placeMark(new Position(0, 0), Mark.X)
            .placeMark(new Position(0, 1), Mark.X)
            .placeMark(new Position(0, 2), Mark.X);

        Optional<List<Position>> line =
            WinChecker.findWinningLineThrough(board, CONFIG_3X3, new Position(0, 1));

        assertThat(line).isPresent();
        assertThat(line.get()).containsExactlyInAnyOrder(
            new Position(0, 0), new Position(0, 1), new Position(0, 2));
    }
}
