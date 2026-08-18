package com.tictactoe.game.use_case;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.tictactoe.game.domain.Board;
import com.tictactoe.game.domain.GameConfig;
import com.tictactoe.game.domain.GameState;
import com.tictactoe.game.domain.Mark;
import com.tictactoe.game.domain.Position;
import com.tictactoe.game.ai.AiStrategy;
import com.tictactoe.game.domain.exception.InvalidMoveException;
import org.junit.jupiter.api.Test;

class RequestAiMoveUseCaseTest {

    private static final GameConfig CONFIG_3X3 = new GameConfig(3, 3);
    private final RequestAiMoveUseCase useCase = new RequestAiMoveUseCase();
    private final AiStrategy fixedMoveStrategy = new AiStrategy() {
        @Override
        public Position selectMove(Board board, GameConfig config, Mark aiMark) {
            return new Position(1, 1);
        }
    };

    @Test
    void executeAsksStrategyForMoveAndAppliesIt() {
        GameState state = GameState.newGame(CONFIG_3X3);

        GameState next = useCase.execute(state, fixedMoveStrategy);

        assertThat(next.board().get(new Position(1, 1))).contains(Mark.X);
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

        assertThatThrownBy(() -> useCase.execute(state, fixedMoveStrategy))
            .isInstanceOf(InvalidMoveException.class);
    }
}
