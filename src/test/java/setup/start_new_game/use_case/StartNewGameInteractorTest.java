package setup.start_new_game.use_case;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import data_access.InMemoryGameSession;
import game.domain.AiDifficulty;
import game.domain.CommonGameStateFactory;
import game.domain.GameConfig;
import game.domain.GameMode;
import game.domain.InProgress;
import game.domain.Mark;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class StartNewGameInteractorTest {

    @Mock
    private StartNewGameOutputBoundary presenter;

    private InMemoryGameSession session;
    private StartNewGameInteractor interactor;

    @BeforeEach
    void setUp() {
        session = new InMemoryGameSession();
        interactor = new StartNewGameInteractor(presenter, new CommonGameStateFactory(), session);
    }

    @Test
    void execute_ValidRequest_PresentsFreshGameStateAndWritesSession() {
        GameConfig config = new GameConfig(4, 3);
        StartNewGameInputData inputData =
            new StartNewGameInputData(4, 3, GameMode.TWO_PLAYER, Optional.<AiDifficulty>empty());

        interactor.execute(inputData);

        ArgumentCaptor<StartNewGameOutputData> captor =
            ArgumentCaptor.forClass(StartNewGameOutputData.class);
        verify(presenter).prepareSuccessView(captor.capture());
        assertThat(captor.getValue().gameState().config()).isEqualTo(config);
        assertThat(captor.getValue().gameState().board().emptyPositions()).hasSize(16);
        assertThat(captor.getValue().gameState().currentTurn()).isEqualTo(Mark.X);
        assertThat(captor.getValue().gameState().status()).isInstanceOf(InProgress.class);
        assertThat(session.getCurrentGameState()).isEqualTo(captor.getValue().gameState());
        assertThat(session.getMode()).isEqualTo(GameMode.TWO_PLAYER);
        assertThat(session.getAiDifficulty()).isEmpty();
        verify(presenter, never()).prepareFailView(any());
    }

    @Test
    void execute_AiMode_WritesDifficultyToSession() {
        StartNewGameInputData inputData = new StartNewGameInputData(
                3, 3, GameMode.HUMAN_VS_AI, Optional.of(AiDifficulty.EASY));

        interactor.execute(inputData);

        assertThat(session.getMode()).isEqualTo(GameMode.HUMAN_VS_AI);
        assertThat(session.getAiDifficulty()).contains(AiDifficulty.EASY);
    }

    @Test
    void execute_InvalidConfig_PresentsFailViewWithoutWritingSession() {
        StartNewGameInputData inputData = new StartNewGameInputData(
                3, 5, GameMode.TWO_PLAYER, Optional.<AiDifficulty>empty());

        interactor.execute(inputData);

        verify(presenter).prepareFailView(
            "winLength (5) must not exceed boardSize (3)");
        verify(presenter, never()).prepareSuccessView(any());
        assertThat(session.getCurrentGameState()).isNull();
    }

    @Test
    void switchToSetupView_DelegatesToPresenter() {
        interactor.switchToSetupView();

        verify(presenter).switchToSetupView();
    }
}