package play.request_ai_move;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;

import data_access.InMemoryGameSession;
import game.domain.AiDifficulty;
import game.domain.GameConfig;
import game.domain.GameMode;
import game.domain.GameState;
import game.domain.Position;
import play.request_ai_move.use_case.RequestAiMoveInputBoundary;
import play.request_ai_move.use_case.RequestAiMoveInputData;
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

    private InMemoryGameSession session;
    private CapturingUiScheduler scheduler;
    private RequestAiMoveController controller;

    @BeforeEach
    void setUp() {
        session = new InMemoryGameSession();
        scheduler = new CapturingUiScheduler();
        controller = new RequestAiMoveController(requestAiMoveUseCase, session, scheduler);
    }

    @Test
    void execute_RunsInteractorInBackgroundWithSnapshotAndDifficulty() {
        GameState current = GameState.newGame(CONFIG_3X3);
        GameState base = current.applyMove(new Position(0, 0));
        session.setCurrentGame(base, GameMode.HUMAN_VS_AI, Optional.of(AiDifficulty.EASY));

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
        GameState current = GameState.newGame(CONFIG_3X3);
        session.setCurrentGameState(current);

        assertThatThrownBy(() -> controller.execute()).isInstanceOf(NullPointerException.class);
    }
}