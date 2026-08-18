package game.make_human_move;

import game.GameState;
import game.GameViewModel;
import game.GameViewModelMapper;
import game.make_human_move.use_case.MakeHumanMoveOutputBoundary;
import game.make_human_move.use_case.MakeHumanMoveOutputData;

/**
 * The Presenter for the Make Human Move Use Case. Updates the shared game
 * view model and fires a property change so the view re-renders; when the
 * move leaves the board on the AI's turn, hands off to the AI move request
 * (wired by the AppBuilder).
 */
public class MakeHumanMovePresenter implements MakeHumanMoveOutputBoundary {

    private final GameViewModel gameViewModel;
    private final Runnable requestAiMove;

    public MakeHumanMovePresenter(GameViewModel gameViewModel, Runnable requestAiMove) {
        this.gameViewModel = gameViewModel;
        this.requestAiMove = requestAiMove;
    }

    @Override
    public void prepareSuccessView(MakeHumanMoveOutputData outputData) {
        final GameState state = gameViewModel.getState();
        state.setCurrentGameState(outputData.updatedState());
        state.setBoard(GameViewModelMapper.toBoardViewModel(outputData.updatedState()));
        state.setStatus(GameViewModelMapper.toStatusViewModel(outputData.updatedState()));
        state.setError(null);
        gameViewModel.firePropertyChanged();

        if (state.isAiTurn()) {
            requestAiMove.run();
        }
    }

    @Override
    public void prepareFailView(String error) {
        final GameState state = gameViewModel.getState();
        state.setError(error);
        gameViewModel.firePropertyChanged();
    }
}