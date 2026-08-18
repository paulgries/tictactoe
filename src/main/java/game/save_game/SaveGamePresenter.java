package game.save_game;

import game.GameViewModel;
import game.save_game.use_case.SaveGameOutputBoundary;
import game.save_game.use_case.SaveGameOutputData;

/**
 * The Presenter for the Save Game Use Case. Confirms the save through the
 * frame's transient message channel; failures go the same way.
 */
public class SaveGamePresenter implements SaveGameOutputBoundary {

    private final GameViewModel gameViewModel;

    public SaveGamePresenter(GameViewModel gameViewModel) {
        this.gameViewModel = gameViewModel;
    }

    @Override
    public void prepareSuccessView(SaveGameOutputData outputData) {
        gameViewModel.getState().setMessage("Game saved");
        gameViewModel.firePropertyChanged();
    }

    @Override
    public void prepareFailView(String error) {
        gameViewModel.getState().setMessage(error);
        gameViewModel.firePropertyChanged();
    }
}