package persistence.load_game.use_case;

import game.GameSessionDataAccess;
import game.domain.SavedGame;
import java.io.IOException;
import java.util.Optional;

/**
 * The Interactor for the Load Game Use Case. Loads the saved session through
 * the persistence boundary, restores it as the application-layer session,
 * and routes "no saved game" and persistence failures through the output
 * boundary, never to the view.
 */
public final class LoadGameInteractor implements LoadGameInputBoundary {

    private final LoadGameOutputBoundary presenter;
    private final LoadGameDataAccess loadGameDataAccess;
    private final GameSessionDataAccess session;

    public LoadGameInteractor(
            LoadGameOutputBoundary presenter,
            LoadGameDataAccess loadGameDataAccess,
            GameSessionDataAccess session) {
        this.presenter = presenter;
        this.loadGameDataAccess = loadGameDataAccess;
        this.session = session;
    }

    @Override
    public void execute(LoadGameInputData inputData) {
        try {
            Optional<SavedGame> savedGame = loadGameDataAccess.load();
            if (savedGame.isEmpty()) {
                presenter.prepareFailView("no saved game found");
            } else {
                SavedGame game = savedGame.get();
                session.setCurrentGame(game.gameState(), game.mode(), game.difficulty());
                presenter.prepareSuccessView(new LoadGameOutputData(game));
            }
        } catch (IOException e) {
            presenter.prepareFailView("could not load the saved game: " + e.getMessage());
        }
    }
}