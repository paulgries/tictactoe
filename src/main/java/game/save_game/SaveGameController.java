package game.save_game;

import game.GameViewModel;
import game.SessionState;
import game.save_game.use_case.SaveGameInputBoundary;
import game.save_game.use_case.SaveGameInputData;

/**
 * The Controller for the Save Game Use Case. Snapshots the current session
 * from the shared view model.
 */
public class SaveGameController {

    private final SaveGameInputBoundary saveGameUseCase;
    private final GameViewModel gameViewModel;

    public SaveGameController(
            SaveGameInputBoundary saveGameUseCase,
            GameViewModel gameViewModel) {
        this.saveGameUseCase = saveGameUseCase;
        this.gameViewModel = gameViewModel;
    }

    public void execute() {
        final SessionState session = gameViewModel.getSession();
        if (session.getCurrentGameState() == null) {
            throw new IllegalStateException("no game in progress to save");
        }
        saveGameUseCase.execute(new SaveGameInputData(
                session.getCurrentGameState(), session.getMode(), session.getDifficulty()));
    }
}