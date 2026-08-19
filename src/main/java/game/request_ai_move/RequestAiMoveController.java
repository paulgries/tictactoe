package game.request_ai_move;

import game.GameSessionDataAccess;
import game.domain.AiDifficulty;
import game.domain.GameState;
import game.request_ai_move.use_case.RequestAiMoveInputBoundary;
import game.request_ai_move.use_case.RequestAiMoveInputData;
import framework.UiScheduler;

/**
 * The Controller for the Request AI Move Use Case. Runs the use case in the
 * background on a snapshot of the current session, read from the
 * application-layer session; the snapshot is passed through the boundary and
 * echoed back in the output so the presenter can discard stale results after
 * a restart.
 */
public class RequestAiMoveController {

    private final RequestAiMoveInputBoundary requestAiMoveUseCase;
    private final GameSessionDataAccess session;
    private final UiScheduler uiScheduler;

    public RequestAiMoveController(
            RequestAiMoveInputBoundary requestAiMoveUseCase,
            GameSessionDataAccess session,
            UiScheduler uiScheduler) {
        this.requestAiMoveUseCase = requestAiMoveUseCase;
        this.session = session;
        this.uiScheduler = uiScheduler;
    }

    public void execute() {
        final GameState base = session.getCurrentGameState();
        final AiDifficulty difficulty = session.getAiDifficulty()
            .orElseThrow(() -> new NullPointerException("no AI difficulty is set"));

        uiScheduler.runInBackground(() -> {
            requestAiMoveUseCase.execute(new RequestAiMoveInputData(base, difficulty));
        });
    }
}