package game.save_game;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

import game.GameViewModel;
import game.domain.AiDifficulty;
import game.domain.GameConfig;
import game.domain.GameMode;
import game.save_game.use_case.SaveGameInputBoundary;
import game.save_game.use_case.SaveGameInputData;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

class SaveGameControllerTest {

    private SaveGameInputBoundary saveGameUseCase;
    private GameViewModel gameViewModel;
    private SaveGameController controller;

    @BeforeEach
    void setUp() {
        saveGameUseCase = mock(SaveGameInputBoundary.class);
        gameViewModel = new GameViewModel();
        controller = new SaveGameController(saveGameUseCase, gameViewModel);
    }

    @Test
    void execute_GameInProgress_DelegatesSessionSnapshot() {
        game.domain.GameState state = game.domain.GameState.newGame(new GameConfig(3, 3));
        gameViewModel.getSession().setCurrentGameState(state);
        gameViewModel.getSession().setMode(GameMode.HUMAN_VS_AI);
        gameViewModel.getSession().setDifficulty(Optional.of(AiDifficulty.DIFFICULT));

        controller.execute();

        ArgumentCaptor<SaveGameInputData> captor =
            ArgumentCaptor.forClass(SaveGameInputData.class);
        verify(saveGameUseCase).execute(captor.capture());
        assertThat(captor.getValue().gameState()).isEqualTo(state);
        assertThat(captor.getValue().mode()).isEqualTo(GameMode.HUMAN_VS_AI);
        assertThat(captor.getValue().difficulty()).contains(AiDifficulty.DIFFICULT);
    }

    @Test
    void execute_NoGameInProgress_ThrowsAndDoesNotDelegate() {
        assertThatThrownBy(() -> controller.execute())
            .isInstanceOf(IllegalStateException.class)
            .hasMessage("no game in progress to save");

        verifyNoInteractions(saveGameUseCase);
    }
}