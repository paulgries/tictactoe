package com.tictactoe.game.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.tictactoe.game.domain.exception.InvalidMoveException;
import com.tictactoe.game.testutil.GameFixtures;
import org.junit.jupiter.api.Test;

class GameStateTest {

    private static final GameConfig CONFIG_3X3 = new GameConfig(3, 3);

    @Test
    void newGameStartsWithEmptyBoardAndXsTurn() {
        GameState state = GameState.newGame(CONFIG_3X3);

        assertThat(state.board().emptyPositions()).hasSize(9);
        assertThat(state.currentTurn()).isEqualTo(Mark.X);
        assertThat(state.status()).isInstanceOf(InProgress.class);
    }

    @Test
    void applyMoveByCurrentPlayerAdvancesTurnToOtherMark() {
        GameState state = GameState.newGame(CONFIG_3X3);

        GameState next = state.applyMove(new Position(0, 0));

        assertThat(next.board().get(new Position(0, 0))).contains(Mark.X);
        assertThat(next.currentTurn()).isEqualTo(Mark.O);
    }

    @Test
    void applyMoveOnOccupiedCellThrowsInvalidMoveException() {
        GameState state = GameState.newGame(CONFIG_3X3).applyMove(new Position(0, 0));

        assertThatThrownBy(() -> state.applyMove(new Position(0, 0)))
            .isInstanceOf(InvalidMoveException.class);
    }

    @Test
    void applyMoveAfterGameOverThrowsInvalidMoveException() {
        GameState state = GameFixtures.wonByX();

        assertThat(state.isGameOver()).isTrue();
        assertThatThrownBy(() -> state.applyMove(new Position(2, 2)))
            .isInstanceOf(InvalidMoveException.class);
    }

    @Test
    void applyMoveThatCompletesLineSetsWinOutcomeWithCorrectWinnerAndLine() {
        GameState state = GameFixtures.wonByX();

        assertThat(state.status()).isInstanceOf(Win.class);
        Win win = (Win) state.status();
        assertThat(win.winner()).isEqualTo(Mark.X);
        assertThat(win.winningLine()).containsExactlyInAnyOrder(
            new Position(0, 0), new Position(0, 1), new Position(0, 2));
    }

    @Test
    void applyMoveThatFillsBoardWithoutWinnerSetsDrawOutcome() {
        GameState state = GameFixtures.drawn();

        assertThat(state.status()).isInstanceOf(Draw.class);
    }

    @Test
    void applyMoveReturnsNewInstanceLeavingOriginalUnchanged() {
        GameState state = GameState.newGame(CONFIG_3X3);

        state.applyMove(new Position(0, 0));

        assertThat(state.board().emptyPositions()).hasSize(9);
        assertThat(state.currentTurn()).isEqualTo(Mark.X);
    }

    @Test
    void isGameOverTrueForWinAndDrawFalseForInProgress() {
        GameState inProgress = GameState.newGame(CONFIG_3X3);
        assertThat(inProgress.isGameOver()).isFalse();

        GameState won = GameFixtures.wonByX();
        assertThat(won.isGameOver()).isTrue();
    }
}
