package com.tictactoe.application.usecase;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.tictactoe.domain.GameConfig;
import com.tictactoe.domain.GameState;
import com.tictactoe.domain.Mark;
import com.tictactoe.domain.Position;
import com.tictactoe.domain.exception.InvalidMoveException;
import org.junit.jupiter.api.Test;

class MakeHumanMoveUseCaseTest {

    private static final GameConfig CONFIG_3X3 = new GameConfig(3, 3);
    private final MakeHumanMoveUseCase useCase = new MakeHumanMoveUseCase();

    @Test
    void executeAppliesValidMoveAndReturnsUpdatedGameState() {
        GameState state = GameState.newGame(CONFIG_3X3);

        GameState next = useCase.execute(state, new Position(0, 0));

        assertThat(next.board().get(new Position(0, 0))).contains(Mark.X);
        assertThat(next.currentTurn()).isEqualTo(Mark.O);
    }

    @Test
    void executeThrowsWhenGameAlreadyOver() {
        GameState state = GameState.newGame(CONFIG_3X3)
            .applyMove(new Position(0, 0)) // X
            .applyMove(new Position(1, 0)) // O
            .applyMove(new Position(0, 1)) // X
            .applyMove(new Position(1, 1)) // O
            .applyMove(new Position(0, 2)); // X wins

        assertThatThrownBy(() -> useCase.execute(state, new Position(2, 2)))
            .isInstanceOf(InvalidMoveException.class);
    }

    @Test
    void executeThrowsWhenCellOccupied() {
        GameState state = GameState.newGame(CONFIG_3X3).applyMove(new Position(0, 0));

        assertThatThrownBy(() -> useCase.execute(state, new Position(0, 0)))
            .isInstanceOf(InvalidMoveException.class);
    }
}
