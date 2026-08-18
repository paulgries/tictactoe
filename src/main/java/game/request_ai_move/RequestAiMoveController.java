package game.request_ai_move;

import game.GameState;
import game.GameViewModel;
import game.ai.AiStrategy;
import game.request_ai_move.use_case.RequestAiMoveInputBoundary;
import game.request_ai_move.use_case.RequestAiMoveInputData;
import framework.UiScheduler;

/**
 * The Controller for the Request AI Move Use Case. Runs the use case in the
 * background on a snapshot of the current session; the snapshot is passed
 * through the boundary and echoed back in the output so the presenter can
 * discard stale results after a restart.
 */
public class RequestAiMoveController {

    private final RequestAiMoveInputBoundary requestAiMoveUseCase;
    private final GameViewModel gameViewModel;
    private final UiScheduler uiScheduler;

    public RequestAiMoveController(
            RequestAiMoveInputBoundary requestAiMoveUseCase,
            GameViewModel gameViewModel,
            UiScheduler uiScheduler) {
        this.requestAiMoveUseCase = requestAiMoveUseCase;
        this.gameViewModel = gameViewModel;
        this.uiScheduler = uiScheduler;
    }

    public void execute() {
        final GameState state = gameViewModel.getState();
        final game.domain.GameState base = state.getCurrentGameState();
        final AiStrategy strategy = state.getAiStrategy()
            .orElseThrow(() -> new NullPointerException("no AI strategy is active"));

        uiScheduler.runInBackground(() -> {
            requestAiMoveUseCase.execute(new RequestAiMoveInputData(base, strategy));
        });
    }
}