package com.tictactoe.game.use_case;

import static org.assertj.core.api.Assertions.assertThat;

import com.tictactoe.game.NewGameRequest;
import com.tictactoe.game.domain.GameConfig;
import com.tictactoe.game.domain.GameMode;
import com.tictactoe.game.domain.GameState;
import com.tictactoe.game.domain.InProgress;
import com.tictactoe.game.domain.Mark;
import java.util.Optional;
import org.junit.jupiter.api.Test;

class StartNewGameUseCaseTest {

    private final StartNewGameUseCase useCase = new StartNewGameUseCase();

    @Test
    void executeReturnsGameStateWithConfiguredBoardSizeAndWinLength() {
        GameConfig config = new GameConfig(4, 3);
        NewGameRequest request = new NewGameRequest(config, GameMode.TWO_PLAYER, Optional.empty());

        GameState state = useCase.execute(request);

        assertThat(state.config()).isEqualTo(config);
        assertThat(state.board().emptyPositions()).hasSize(16);
    }

    @Test
    void executeStartsWithEmptyBoardXsTurnAndInProgressStatus() {
        GameConfig config = new GameConfig(3, 3);
        NewGameRequest request = new NewGameRequest(config, GameMode.HUMAN_VS_AI, Optional.empty());

        GameState state = useCase.execute(request);

        assertThat(state.currentTurn()).isEqualTo(Mark.X);
        assertThat(state.status()).isInstanceOf(InProgress.class);
    }
}
