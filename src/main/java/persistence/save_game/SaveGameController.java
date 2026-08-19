package persistence.save_game;

import persistence.save_game.use_case.SaveGameInputBoundary;
import persistence.save_game.use_case.SaveGameInputData;

/**
 * The Controller for the Save Game Use Case. The session to save comes from
 * the application-layer session, so there is no view input to build.
 */
public class SaveGameController {

    private final SaveGameInputBoundary saveGameUseCase;

    public SaveGameController(SaveGameInputBoundary saveGameUseCase) {
        this.saveGameUseCase = saveGameUseCase;
    }

    public void execute() {
        saveGameUseCase.execute(new SaveGameInputData());
    }
}