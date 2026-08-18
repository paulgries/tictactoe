package game.request_ai_move;

import game.GameState;
import game.GameViewModel;
import game.GameViewModelMapper;
import game.request_ai_move.use_case.RequestAiMoveOutputBoundary;
import game.request_ai_move.use_case.RequestAiMoveOutputData;
import framework.UiScheduler;

/**
 * The Presenter for the Request AI Move Use Case. Applies the AI move on the
 * UI thread, discarding the result if the session moved on (e.g. a restart)
 * while the move was being computed in the background; staleness is checked
 * against the base the result was computed from, which travels in the
 * output data.
 */
public class RequestAiMovePresenter implements RequestAiMoveOutputBoundary {

    private final GameViewModel gameViewModel;
    private final UiScheduler uiScheduler;

    public RequestAiMovePresenter(GameViewModel gameViewModel, UiScheduler uiScheduler) {
        this.gameViewModel = gameViewModel;
        this.uiScheduler = uiScheduler;
    }

    @Override
    public void prepareSuccessView(RequestAiMoveOutputData outputData) {
        uiScheduler.runOnUiThread(() -> {
            final GameState state = gameViewModel.getState();
            if (state.getCurrentGameState().equals(outputData.base())) {
                state.setCurrentGameState(outputData.updatedState());
                state.setBoard(GameViewModelMapper.toBoardViewModel(outputData.updatedState()));
                state.setStatus(GameViewModelMapper.toStatusViewModel(outputData.updatedState()));
                state.setError(null);
                gameViewModel.firePropertyChanged();
            }
        });
    }

    @Override
    public void prepareFailView(String error) {
        uiScheduler.runOnUiThread(() -> {
            final GameState state = gameViewModel.getState();
            state.setError(error);
            gameViewModel.firePropertyChanged();
        });
    }
}