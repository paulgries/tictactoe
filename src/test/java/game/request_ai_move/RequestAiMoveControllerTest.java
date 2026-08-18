package game.request_ai_move;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;

import game.GameViewModel;
import game.domain.AiDifficulty;
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
    void execute_RunsInteractorInBackgroundWithSnapshotAndDifficulty() {
        game.domain.GameState current = game.domain.GameState.newGame(CONFIG_3X3);
        game.domain.GameState base = current.applyMove(new Position(0, 0));
        gameViewModel.getSession().setCurrentGameState(base);
        gameViewModel.getSession().setDifficulty(Optional.of(AiDifficulty.EASY));

        controller.execute();

        assertThat(scheduler.pendingBackgroundTasks()).isEqualTo(1);

        scheduler.runNextBackgroundTask();

        ArgumentCaptor<RequestAiMoveInputData> captor =
            ArgumentCaptor.forClass(RequestAiMoveInputData.class);
        verify(requestAiMoveUseCase).execute(captor.capture());
        assertThat(captor.getValue().state()).isEqualTo(base);
        assertThat(captor.getValue().difficulty()).isEqualTo(AiDifficulty.EASY);
    }

    @Test
    void execute_NoDifficulty_Throws() {
        game.domain.GameState current = game.domain.GameState.newGame(CONFIG_3X3);
        gameViewModel.getSession().setCurrentGameState(current);

        assertThatThrownBy(() -> controller.execute()).isInstanceOf(NullPointerException.class);
    }
}