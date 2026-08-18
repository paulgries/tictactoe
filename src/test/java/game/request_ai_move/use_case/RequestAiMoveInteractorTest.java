package game.request_ai_move.use_case;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import game.ai.AiStrategy;
import game.domain.Board;
import game.domain.GameConfig;
import game.domain.GameState;
import game.domain.Mark;
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

    @Mock
    private AiStrategy strategy;

    private RequestAiMoveInteractor interactor;

    @BeforeEach
    void setUp() {
        interactor = new RequestAiMoveInteractor(presenter);
    }

    @Test
    void execute_GameAlreadyOver_PresentsFailViewWithoutConsultingStrategy() {
        GameState state = GameFixtures.wonByX();

        interactor.execute(new RequestAiMoveInputData(state, strategy));

        verify(presenter).prepareFailView("cannot request an AI move after the game is over");
        verify(presenter, never()).prepareSuccessView(any());
        verify(strategy, never()).selectMove(any(Board.class), any(GameConfig.class), any(Mark.class));
    }

    @Test
    void execute_ValidMove_PresentsUpdatedStateEchoingTheBase() {
        GameState base = GameState.newGame(new GameConfig(3, 3)).applyMove(new Position(0, 0));
        when(strategy.selectMove(base.board(), base.config(), base.currentTurn()))
            .thenReturn(new Position(1, 1));

        interactor.execute(new RequestAiMoveInputData(base, strategy));

        ArgumentCaptor<RequestAiMoveOutputData> captor =
            ArgumentCaptor.forClass(RequestAiMoveOutputData.class);
        verify(presenter).prepareSuccessView(captor.capture());
        assertThat(captor.getValue().updatedState().board().get(new Position(1, 1)))
            .contains(Mark.O);
        assertThat(captor.getValue().updatedState().currentTurn()).isEqualTo(Mark.X);
        assertThat(captor.getValue().base()).isSameAs(base);
    }
}