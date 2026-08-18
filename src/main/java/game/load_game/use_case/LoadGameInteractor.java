package game.load_game.use_case;

import game.domain.SavedGame;
import java.io.IOException;
import java.util.Optional;

/**
 * The Interactor for the Load Game Use Case. Loads the saved session through
 * the persistence boundary and routes "no saved game" and persistence
 * failures through the output boundary, never to the view.
 */
public final class LoadGameInteractor implements LoadGameInputBoundary {

    private final LoadGameOutputBoundary presenter;
    private final LoadGameDataAccess loadGameDataAccess;

    public LoadGameInteractor(
            LoadGameOutputBoundary presenter,
            LoadGameDataAccess loadGameDataAccess) {
        this.presenter = presenter;
        this.loadGameDataAccess = loadGameDataAccess;
    }

    @Override
    public void execute(LoadGameInputData inputData) {
        try {
            Optional<SavedGame> savedGame = loadGameDataAccess.load();
            if (savedGame.isEmpty()) {
                presenter.prepareFailView("no saved game found");
            } else {
                presenter.prepareSuccessView(new LoadGameOutputData(savedGame.get()));
            }
        } catch (IOException e) {
            presenter.prepareFailView("could not load the saved game: " + e.getMessage());
        }
    }
}