package game.request_ai_move;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;

import game.GameViewModel;
import game.ai.AiStrategy;
import game.domain.GameConfig;
import game.domain.GameState;
import game.domain.Position;
import game.request_ai_move.use_case.RequestAiMoveInputBoundary;
import game.request_ai_move.use_case.RequestAiMoveInputData;
import game.testutil.CapturingUiScheduler;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class RequestAiMoveControllerTest {

    private static final GameConfig CONFIG_3X3 = new GameConfig(3, 3);

    private final AiStrategy fixedMoveStrategy = new AiStrategy() {
        @Override
        public Position selectMove(game.domain.Board board, GameConfig config, game.domain.Mark aiMark) {
            return new Position(1, 1);
        }
    };

    @Mock
    private RequestAiMoveInputBoundary requestAiMoveUseCase;

    private GameViewModel gameViewModel;
    private CapturingUiScheduler scheduler;
    private RequestAiMoveController controller;

    @BeforeEach
    void setUp() {
        gameViewModel = new GameViewModel();
        scheduler = new CapturingUiScheduler();
        controller = new RequestAiMoveController(requestAiMoveUseCase, gameViewModel, scheduler);
    }

    @Test
    void execute_MarksPendingBaseAndRunsInteractorInBackgroundWithSnapshotAndStrategy() {
        game.domain.GameState current = game.domain.GameState.newGame(CONFIG_3X3);
        game.domain.GameState base = current.applyMove(new Position(0, 0));
        gameViewModel.getState().setCurrentGameState(base);
        gameViewModel.getState().setAiStrategy(Optional.of(fixedMoveStrategy));

        controller.execute();

        assertThat(gameViewModel.getState().getPendingAiBase()).isEqualTo(base);
        assertThat(scheduler.pendingBackgroundTasks()).isEqualTo(1);

        scheduler.runNextBackgroundTask();

        ArgumentCaptor<RequestAiMoveInputData> captor =
            ArgumentCaptor.forClass(RequestAiMoveInputData.class);
        verify(requestAiMoveUseCase).execute(captor.capture());
        assertThat(captor.getValue().state()).isEqualTo(base);
        assertThat(captor.getValue().strategy()).isEqualTo(fixedMoveStrategy);
    }

    @Test
    void execute_NoStrategy_Throws() {
        game.domain.GameState current = game.domain.GameState.newGame(CONFIG_3X3);
        gameViewModel.getState().setCurrentGameState(current);

        assertThatThrownBy(() -> controller.execute()).isInstanceOf(NullPointerException.class);
    }
}