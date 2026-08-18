package game.start_new_game;

import game.domain.AiDifficulty;
import game.domain.GameConfig;
import game.domain.GameMode;
import game.start_new_game.use_case.StartNewGameInputBoundary;
import game.start_new_game.use_case.StartNewGameInputData;
import java.util.Optional;

/**
 * The Controller for the Start New Game Use Case. Builds the input data from
 * primitives (as the CAWithBuilder controllers do) and remembers the last
 * input so the view can request a restart with the same settings.
 */
public class StartNewGameController {

    private final StartNewGameInputBoundary startNewGameUseCase;

    private StartNewGameInputData lastInput;

    public StartNewGameController(StartNewGameInputBoundary startNewGameUseCase) {
        this.startNewGameUseCase = startNewGameUseCase;
    }

    public void execute(int boardSize, int winLength, GameMode mode,
                        Optional<AiDifficulty> aiDifficulty) {
        final StartNewGameInputData inputData = new StartNewGameInputData(
                new GameConfig(boardSize, winLength), mode, aiDifficulty);
        this.lastInput = inputData;

        startNewGameUseCase.execute(inputData);
    }

    public void restart() {
        startNewGameUseCase.execute(lastInput);
    }

    public void switchToSetupView() {
        startNewGameUseCase.switchToSetupView();
    }
}