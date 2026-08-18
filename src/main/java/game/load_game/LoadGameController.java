package game.load_game;

import game.load_game.use_case.LoadGameInputBoundary;
import game.load_game.use_case.LoadGameInputData;

/**
 * The Controller for the Load Game Use Case. Takes no input: there is one
 * saved game slot.
 */
public class LoadGameController {

    private final LoadGameInputBoundary loadGameUseCase;

    public LoadGameController(LoadGameInputBoundary loadGameUseCase) {
        this.loadGameUseCase = loadGameUseCase;
    }

    public void execute() {
        loadGameUseCase.execute(new LoadGameInputData());
    }
}