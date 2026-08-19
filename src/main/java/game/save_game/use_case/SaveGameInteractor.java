package game.save_game.use_case;

import game.GameSessionDataAccess;
import game.domain.GameState;
import game.domain.SavedGame;
import java.io.IOException;

/**
 * The Interactor for the Save Game Use Case. Hands the session snapshot from
 * the application-layer session to the persistence boundary and routes
 * persistence failures through the output boundary, never to the view.
 */
public final class SaveGameInteractor implements SaveGameInputBoundary {

    private final SaveGameOutputBoundary presenter;
    private final SaveGameDataAccess saveGameDataAccess;
    private final GameSessionDataAccess session;

    public SaveGameInteractor(
            SaveGameOutputBoundary presenter,
            SaveGameDataAccess saveGameDataAccess,
            GameSessionDataAccess session) {
        this.presenter = presenter;
        this.saveGameDataAccess = saveGameDataAccess;
        this.session = session;
    }

    @Override
    public void execute(SaveGameInputData inputData) {
        final GameState state = session.getCurrentGameState();
        if (state == null) {
            presenter.prepareFailView("no game in progress to save");
            return;
        }
        try {
            saveGameDataAccess.save(new SavedGame(state, session.getMode(), session.getAiDifficulty()));
            presenter.prepareSuccessView(new SaveGameOutputData());
        } catch (IOException e) {
            presenter.prepareFailView("could not save the game: " + e.getMessage());
        }
    }
}