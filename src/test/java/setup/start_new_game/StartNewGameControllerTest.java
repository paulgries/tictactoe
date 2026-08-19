package setup.start_new_game;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import game.domain.AiDifficulty;
import game.domain.GameMode;
import setup.start_new_game.use_case.StartNewGameInputBoundary;
import setup.start_new_game.use_case.StartNewGameInputData;
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

    private StartNewGameController controller;

    @BeforeEach
    void setUp() {
        controller = new StartNewGameController(startNewGameUseCase);
    }

    @Test
    void execute_BuildsInputDataFromPrimitives() {
        controller.execute(4, 3, GameMode.HUMAN_VS_AI, Optional.of(AiDifficulty.EASY));

        ArgumentCaptor<StartNewGameInputData> captor =
            ArgumentCaptor.forClass(StartNewGameInputData.class);
        verify(startNewGameUseCase).execute(captor.capture());
        assertThat(captor.getValue().boardSize()).isEqualTo(4);
        assertThat(captor.getValue().winLength()).isEqualTo(3);
        assertThat(captor.getValue().mode()).isEqualTo(GameMode.HUMAN_VS_AI);
        assertThat(captor.getValue().aiDifficulty()).contains(AiDifficulty.EASY);
    }

    @Test
    void execute_TwoPlayerModeCarriesEmptyDifficulty() {
        controller.execute(3, 3, GameMode.TWO_PLAYER, Optional.empty());

        ArgumentCaptor<StartNewGameInputData> captor =
            ArgumentCaptor.forClass(StartNewGameInputData.class);
        verify(startNewGameUseCase).execute(captor.capture());
        assertThat(captor.getValue().aiDifficulty()).isEmpty();
    }

    @Test
    void restart_ReexecutesTheLastInput() {
        controller.execute(3, 3, GameMode.TWO_PLAYER, Optional.empty());

        controller.restart();

        ArgumentCaptor<StartNewGameInputData> captor =
            ArgumentCaptor.forClass(StartNewGameInputData.class);
        verify(startNewGameUseCase, times(2)).execute(captor.capture());
        assertThat(captor.getAllValues()).extracting(StartNewGameInputData::boardSize)
            .containsOnly(captor.getAllValues().get(0).boardSize());
        assertThat(captor.getValue().mode()).isEqualTo(GameMode.TWO_PLAYER);
    }

    @Test
    void restart_BeforeAnyExecute_Throws() {
        assertThatThrownBy(() -> controller.restart())
            .isInstanceOf(IllegalStateException.class)
            .hasMessage("cannot restart before a game has been started");
        verify(startNewGameUseCase, never()).execute(any());
    }

    @Test
    void switchToSetupView_DelegatesToUseCase() {
        controller.switchToSetupView();

        verify(startNewGameUseCase).switchToSetupView();
    }
}