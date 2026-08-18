package game.start_new_game.use_case;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import game.ai.AiStrategyFactory;
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

    private StartNewGameInteractor interactor;

    @BeforeEach
    void setUp() {
        interactor = new StartNewGameInteractor(
                presenter, new CommonGameStateFactory(), new AiStrategyFactory());
    }

    @Test
    void execute_ValidRequest_PresentsFreshGameState() {
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
        assertThat(captor.getValue().mode()).isEqualTo(GameMode.TWO_PLAYER);
        assertThat(captor.getValue().aiStrategy()).isEmpty();
        verify(presenter, never()).prepareFailView(any());
    }

    @Test
    void execute_AiMode_CreatesStrategyFromDifficulty() {
        StartNewGameInputData inputData = new StartNewGameInputData(
                3, 3, GameMode.HUMAN_VS_AI, Optional.of(AiDifficulty.EASY));

        interactor.execute(inputData);

        ArgumentCaptor<StartNewGameOutputData> captor =
            ArgumentCaptor.forClass(StartNewGameOutputData.class);
        verify(presenter).prepareSuccessView(captor.capture());
        assertThat(captor.getValue().mode()).isEqualTo(GameMode.HUMAN_VS_AI);
        assertThat(captor.getValue().aiStrategy()).isPresent();
    }

    @Test
    void execute_InvalidConfig_PresentsFailViewWithoutSuccessView() {
        StartNewGameInputData inputData = new StartNewGameInputData(
                3, 5, GameMode.TWO_PLAYER, Optional.<AiDifficulty>empty());

        interactor.execute(inputData);

        verify(presenter).prepareFailView(
            "winLength (5) must not exceed boardSize (3)");
        verify(presenter, never()).prepareSuccessView(any());
    }

    @Test
    void switchToSetupView_DelegatesToPresenter() {
        interactor.switchToSetupView();

        verify(presenter).switchToSetupView();
    }
}