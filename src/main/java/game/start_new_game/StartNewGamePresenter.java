package game.start_new_game;

import framework.ViewManagerModel;
import game.GameState;
import game.GameViewModel;
import game.GameViewModelMapper;
import game.start_new_game.use_case.StartNewGameOutputBoundary;
import game.start_new_game.use_case.StartNewGameOutputData;

/**
 * The Presenter for the Start New Game Use Case. Stashes the session data
 * (mode and AI strategy) in the shared bean, updates the render state, and
 * fires a property change so the view re-renders. Navigation is
 * presenter-driven through the {@link ViewManagerModel}, as in
 * CAWithBuilder.
 */
public class StartNewGamePresenter implements StartNewGameOutputBoundary {

    private final GameViewModel gameViewModel;
    private final ViewManagerModel viewManagerModel;
    private final String setupViewName;

    public StartNewGamePresenter(
            GameViewModel gameViewModel,
            ViewManagerModel viewManagerModel,
            String setupViewName) {
        this.gameViewModel = gameViewModel;
        this.viewManagerModel = viewManagerModel;
        this.setupViewName = setupViewName;
    }

    @Override
    public void prepareSuccessView(StartNewGameOutputData outputData) {
        final GameState state = gameViewModel.getState();
        state.setCurrentGameState(outputData.gameState());
        state.setMode(outputData.mode());
        state.setAiStrategy(outputData.aiStrategy());
        state.setBoard(GameViewModelMapper.toBoardViewModel(outputData.gameState()));
        state.setStatus(GameViewModelMapper.toStatusViewModel(outputData.gameState()));
        state.setError(null);
        gameViewModel.firePropertyChanged();

        viewManagerModel.setState(gameViewModel.getViewName());
        viewManagerModel.firePropertyChanged();
    }

    @Override
    public void prepareFailView(String error) {
        final GameState state = gameViewModel.getState();
        state.setError(error);
        gameViewModel.firePropertyChanged();
    }

    @Override
    public void switchToSetupView() {
        viewManagerModel.setState(setupViewName);
        viewManagerModel.firePropertyChanged();
    }
}