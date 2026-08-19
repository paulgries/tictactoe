package setup.start_new_game;

import framework.ViewManagerModel;
import play.GameRenderState;
import play.GameViewModel;
import play.GameViewModelMapper;
import setup.SetupViewModel;
import setup.start_new_game.use_case.StartNewGameOutputBoundary;
import setup.start_new_game.use_case.StartNewGameOutputData;

/**
 * The Presenter for the Start New Game Use Case. Renders the fresh game
 * state from the output data and fires a property change so the game view
 * re-renders. Failures go to the setup view model, whose view shows them.
 * Navigation is presenter-driven through the {@link ViewManagerModel}, as in
 * CAWithBuilder.
 */
public class StartNewGamePresenter implements StartNewGameOutputBoundary {

    private final GameViewModel gameViewModel;
    private final SetupViewModel setupViewModel;
    private final ViewManagerModel viewManagerModel;

    public StartNewGamePresenter(
            GameViewModel gameViewModel,
            SetupViewModel setupViewModel,
            ViewManagerModel viewManagerModel) {
        this.gameViewModel = gameViewModel;
        this.setupViewModel = setupViewModel;
        this.viewManagerModel = viewManagerModel;
    }

    @Override
    public void prepareSuccessView(StartNewGameOutputData outputData) {
        final GameRenderState render = gameViewModel.getState();
        render.setBoard(GameViewModelMapper.toBoardRenderState(outputData.gameState()));
        render.setStatus(GameViewModelMapper.toStatusRenderState(outputData.gameState()));
        render.setMessage(null);
        gameViewModel.firePropertyChanged();

        viewManagerModel.setState(gameViewModel.getViewName());
        viewManagerModel.firePropertyChanged();
    }

    @Override
    public void prepareFailView(String error) {
        setupViewModel.getState().setMessage(error);
        setupViewModel.firePropertyChanged();
    }

    @Override
    public void switchToSetupView() {
        viewManagerModel.setState(setupViewModel.getViewName());
        viewManagerModel.firePropertyChanged();
    }
}