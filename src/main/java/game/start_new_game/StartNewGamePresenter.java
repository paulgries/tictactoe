package game.start_new_game;

import game.GameState;
import game.GameViewModel;
import game.GameViewModelMapper;
import game.start_new_game.use_case.StartNewGameOutputBoundary;
import game.start_new_game.use_case.StartNewGameOutputData;

/**
 * The Presenter for the Start New Game Use Case. Updates the shared game
 * view model and fires a property change so the view re-renders.
 */
public class StartNewGamePresenter implements StartNewGameOutputBoundary {

    private final GameViewModel gameViewModel;

    public StartNewGamePresenter(GameViewModel gameViewModel) {
        this.gameViewModel = gameViewModel;
    }

    @Override
    public void prepareSuccessView(StartNewGameOutputData outputData) {
        final GameState state = gameViewModel.getState();
        state.setCurrentGameState(outputData.gameState());
        state.setBoard(GameViewModelMapper.toBoardViewModel(outputData.gameState()));
        state.setStatus(GameViewModelMapper.toStatusViewModel(outputData.gameState()));
        state.setError(null);
        gameViewModel.firePropertyChanged();
    }

    @Override
    public void prepareFailView(String error) {
        final GameState state = gameViewModel.getState();
        state.setError(error);
        gameViewModel.firePropertyChanged();
    }
}