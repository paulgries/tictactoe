package game.load_game;

import framework.ViewManagerModel;
import game.GameRenderState;
import game.GameViewModel;
import game.GameViewModelMapper;
import game.SessionState;
import game.domain.SavedGame;
import game.load_game.use_case.LoadGameOutputBoundary;
import game.load_game.use_case.LoadGameOutputData;

/**
 * The Presenter for the Load Game Use Case. Restores the session and render
 * halves of the shared view model and navigates to the game screen on
 * success; failures go through the frame's transient message channel.
 */
public class LoadGamePresenter implements LoadGameOutputBoundary {

    private final GameViewModel gameViewModel;
    private final ViewManagerModel viewManagerModel;

    public LoadGamePresenter(
            GameViewModel gameViewModel,
            ViewManagerModel viewManagerModel) {
        this.gameViewModel = gameViewModel;
        this.viewManagerModel = viewManagerModel;
    }

    @Override
    public void prepareSuccessView(LoadGameOutputData outputData) {
        final SavedGame savedGame = outputData.savedGame();
        final SessionState session = gameViewModel.getSession();
        session.setCurrentGameState(savedGame.gameState());
        session.setMode(savedGame.mode());
        session.setDifficulty(savedGame.difficulty());
        final GameRenderState render = gameViewModel.getState();
        render.setBoard(GameViewModelMapper.toBoardViewModel(savedGame.gameState()));
        render.setStatus(GameViewModelMapper.toStatusViewModel(savedGame.gameState()));
        render.setMessage(null);
        gameViewModel.firePropertyChanged();

        viewManagerModel.setState(gameViewModel.getViewName());
        viewManagerModel.firePropertyChanged();
    }

    @Override
    public void prepareFailView(String error) {
        gameViewModel.getState().setMessage(error);
        gameViewModel.firePropertyChanged();
    }
}