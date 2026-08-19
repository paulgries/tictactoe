package game.load_game;

import framework.ViewManagerModel;
import game.GameRenderState;
import game.GameViewModel;
import game.GameViewModelMapper;
import game.domain.SavedGame;
import game.load_game.use_case.LoadGameOutputBoundary;
import game.load_game.use_case.LoadGameOutputData;
import game.setup.SetupViewModel;

/**
 * The Presenter for the Load Game Use Case. Renders the restored game from
 * the output data and navigates to the game screen on success; failures go
 * to the setup view model, whose view shows them. The session itself was
 * restored by the interactor.
 */
public class LoadGamePresenter implements LoadGameOutputBoundary {

    private final GameViewModel gameViewModel;
    private final SetupViewModel setupViewModel;
    private final ViewManagerModel viewManagerModel;

    public LoadGamePresenter(
            GameViewModel gameViewModel,
            SetupViewModel setupViewModel,
            ViewManagerModel viewManagerModel) {
        this.gameViewModel = gameViewModel;
        this.setupViewModel = setupViewModel;
        this.viewManagerModel = viewManagerModel;
    }

    @Override
    public void prepareSuccessView(LoadGameOutputData outputData) {
        final SavedGame savedGame = outputData.savedGame();
        final GameRenderState render = gameViewModel.getState();
        render.setBoard(GameViewModelMapper.toBoardRenderState(savedGame.gameState()));
        render.setStatus(GameViewModelMapper.toStatusRenderState(savedGame.gameState()));
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
}