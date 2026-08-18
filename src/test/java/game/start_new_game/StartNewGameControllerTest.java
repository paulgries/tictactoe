package game.start_new_game;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import game.GameState;
import game.GameViewModel;
import game.ai.AiStrategyFactory;
import game.domain.AiDifficulty;
import game.domain.GameMode;
import game.start_new_game.use_case.StartNewGameInputBoundary;
import game.start_new_game.use_case.StartNewGameInputData;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class StartNewGameControllerTest {

    @Mock
    private StartNewGameInputBoundary startNewGameUseCase;

    private GameViewModel gameViewModel;
    private StartNewGameController controller;

    @BeforeEach
    void setUp() {
        gameViewModel = new GameViewModel();
        controller = new StartNewGameController(startNewGameUseCase, new AiStrategyFactory(), gameViewModel);
    }

    @Test
    void execute_BuildsInputDataAndStashesSessionForAiMode() {
        controller.execute(4, 3, GameMode.HUMAN_VS_AI, Optional.of(AiDifficulty.EASY));

        ArgumentCaptor<StartNewGameInputData> captor =
            ArgumentCaptor.forClass(StartNewGameInputData.class);
        verify(startNewGameUseCase).execute(captor.capture());
        assertThat(captor.getValue().config().boardSize()).isEqualTo(4);
        assertThat(captor.getValue().config().winLength()).isEqualTo(3);
        assertThat(captor.getValue().mode()).isEqualTo(GameMode.HUMAN_VS_AI);
        assertThat(captor.getValue().aiDifficulty()).contains(AiDifficulty.EASY);

        GameState state = gameViewModel.getState();
        assertThat(state.getMode()).isEqualTo(GameMode.HUMAN_VS_AI);
        assertThat(state.getAiStrategy()).isPresent();
    }

    @Test
    void execute_TwoPlayerModeLeavesNoStrategy() {
        controller.execute(3, 3, GameMode.TWO_PLAYER, Optional.empty());

        assertThat(gameViewModel.getState().getAiStrategy()).isEmpty();
    }

    @Test
    void restart_ReexecutesTheLastInput() {
        controller.execute(3, 3, GameMode.TWO_PLAYER, Optional.empty());

        controller.restart();

        ArgumentCaptor<StartNewGameInputData> captor =
            ArgumentCaptor.forClass(StartNewGameInputData.class);
        verify(startNewGameUseCase, times(2)).execute(captor.capture());
        assertThat(captor.getAllValues()).extracting(StartNewGameInputData::config)
            .containsOnly(captor.getAllValues().get(0).config());
        assertThat(captor.getValue().mode()).isEqualTo(GameMode.TWO_PLAYER);
    }
}