package game.start_new_game;

import framework.ViewManagerModel;
import game.GameRenderState;
import game.GameViewModel;
import game.GameViewModelMapper;
import game.SessionState;
import game.start_new_game.use_case.StartNewGameOutputBoundary;
import game.start_new_game.use_case.StartNewGameOutputData;

/**
 * The Presenter for the Start New Game Use Case. Stashes the session data
 * (mode and AI difficulty) in the shared view model, updates the render
 * state, and fires a property change so the view re-renders. Navigation is
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
        final SessionState session = gameViewModel.getSession();
        session.setCurrentGameState(outputData.gameState());
        session.setMode(outputData.mode());
        session.setDifficulty(outputData.difficulty());
        final GameRenderState render = gameViewModel.getState();
        render.setBoard(GameViewModelMapper.toBoardViewModel(outputData.gameState()));
        render.setStatus(GameViewModelMapper.toStatusViewModel(outputData.gameState()));
        render.setError(null);
        gameViewModel.firePropertyChanged();

        viewManagerModel.setState(gameViewModel.getViewName());
        viewManagerModel.firePropertyChanged();
    }

    @Override
    public void prepareFailView(String error) {
        gameViewModel.getState().setError(error);
        gameViewModel.firePropertyChanged();
    }

    @Override
    public void switchToSetupView() {
        viewManagerModel.setState(setupViewName);
        viewManagerModel.firePropertyChanged();
    }
}