package play.request_ai_move;

import play.GameRenderState;
import game.GameSessionDataAccess;
import play.GameViewModel;
import play.GameViewModelMapper;
import play.request_ai_move.use_case.RequestAiMoveOutputBoundary;
import play.request_ai_move.use_case.RequestAiMoveOutputData;
import framework.UiScheduler;

/**
 * The Presenter for the Request AI Move Use Case. Applies the AI move on the
 * UI thread, discarding the result if the session moved on (e.g. a restart)
 * while the move was being computed in the background; staleness is checked
 * against the base the result was computed from, which travels in the output
 * data.
 */
public class RequestAiMovePresenter implements RequestAiMoveOutputBoundary {

    private final GameViewModel gameViewModel;
    private final UiScheduler uiScheduler;
    private final GameSessionDataAccess session;

    public RequestAiMovePresenter(
            GameViewModel gameViewModel,
            UiScheduler uiScheduler,
            GameSessionDataAccess session) {
        this.gameViewModel = gameViewModel;
        this.uiScheduler = uiScheduler;
        this.session = session;
    }

    @Override
    public void prepareSuccessView(RequestAiMoveOutputData outputData) {
        uiScheduler.runOnUiThread(() -> {
            if (session.getCurrentGameState().equals(outputData.base())) {
                session.setCurrentGameState(outputData.updatedState());
                final GameRenderState render = gameViewModel.getState();
                render.setBoard(GameViewModelMapper.toBoardRenderState(outputData.updatedState()));
                render.setStatus(GameViewModelMapper.toStatusRenderState(outputData.updatedState()));
                render.setMessage(null);
                gameViewModel.firePropertyChanged();
            }
        });
    }

    @Override
    public void prepareFailView(String error) {
        uiScheduler.runOnUiThread(() -> {
            gameViewModel.getState().setMessage(error);
            gameViewModel.firePropertyChanged();
        });
    }
}