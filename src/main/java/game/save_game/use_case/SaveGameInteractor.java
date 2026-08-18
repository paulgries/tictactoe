package game.save_game.use_case;

import game.domain.SavedGame;
import java.io.IOException;

/**
 * The Interactor for the Save Game Use Case. Hands the session snapshot to
 * the persistence boundary and routes persistence failures through the
 * output boundary, never to the view.
 */
public final class SaveGameInteractor implements SaveGameInputBoundary {

    private final SaveGameOutputBoundary presenter;
    private final SaveGameDataAccess saveGameDataAccess;

    public SaveGameInteractor(
            SaveGameOutputBoundary presenter,
            SaveGameDataAccess saveGameDataAccess) {
        this.presenter = presenter;
        this.saveGameDataAccess = saveGameDataAccess;
    }

    @Override
    public void execute(SaveGameInputData inputData) {
        try {
            saveGameDataAccess.save(new SavedGame(
                    inputData.gameState(), inputData.mode(), inputData.difficulty()));
            presenter.prepareSuccessView(new SaveGameOutputData());
        } catch (IOException e) {
            presenter.prepareFailView("could not save the game: " + e.getMessage());
        }
    }
}