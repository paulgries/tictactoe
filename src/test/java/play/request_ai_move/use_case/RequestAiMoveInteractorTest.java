package play.request_ai_move.use_case;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import game.ai.CommonAiStrategyFactory;
import game.ai.EasyAiStrategy;
import game.domain.AiDifficulty;
import game.domain.GameConfig;
import game.domain.GameState;
import game.domain.Position;
import game.testutil.GameFixtures;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class RequestAiMoveInteractorTest {

    @Mock
    private RequestAiMoveOutputBoundary presenter;

    private RequestAiMoveInteractor interactor;

    @BeforeEach
    void setUp() {
        interactor = new RequestAiMoveInteractor(presenter, new CommonAiStrategyFactory());
    }

    @Test
    void execute_GameAlreadyOver_PresentsFailViewWithoutConsultingStrategy() {
        GameState state = GameFixtures.wonByX();

        interactor.execute(new RequestAiMoveInputData(state, AiDifficulty.EASY));

        verify(presenter).prepareFailView("cannot request an AI move after the game is over");
        verify(presenter, never()).prepareSuccessView(any());
    }

    @Test
    void execute_ValidMove_BuildsStrategyFromDifficultyAndPresentsUpdatedStateEchoingTheBase() {
        GameState base = GameState.newGame(new GameConfig(3, 3)).applyMove(new Position(0, 0));

        interactor.execute(new RequestAiMoveInputData(base, AiDifficulty.MEDIUM));

        ArgumentCaptor<RequestAiMoveOutputData> captor =
            ArgumentCaptor.forClass(RequestAiMoveOutputData.class);
        verify(presenter).prepareSuccessView(captor.capture());
        Position expectedMove = new EasyAiStrategy()
            .selectMove(base.board(), base.config(), base.currentTurn());
        assertThat(captor.getValue().updatedState()).isEqualTo(base.applyMove(expectedMove));
        assertThat(captor.getValue().base()).isSameAs(base);
    }
}