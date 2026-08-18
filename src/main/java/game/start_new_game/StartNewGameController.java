package game.start_new_game;

import game.GameState;
import game.GameViewModel;
import game.ai.AiStrategyFactory;
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
    private final AiStrategyFactory aiStrategyFactory;
    private final GameViewModel gameViewModel;

    private StartNewGameInputData lastInput;

    public StartNewGameController(
            StartNewGameInputBoundary startNewGameUseCase,
            AiStrategyFactory aiStrategyFactory,
            GameViewModel gameViewModel) {
        this.startNewGameUseCase = startNewGameUseCase;
        this.aiStrategyFactory = aiStrategyFactory;
        this.gameViewModel = gameViewModel;
    }

    public void execute(int boardSize, int winLength, GameMode mode,
                        Optional<AiDifficulty> aiDifficulty) {
        final StartNewGameInputData inputData = new StartNewGameInputData(
                new GameConfig(boardSize, winLength), mode, aiDifficulty);
        this.lastInput = inputData;

        final GameState state = gameViewModel.getState();
        state.setMode(mode);
        state.setAiStrategy(mode == GameMode.HUMAN_VS_AI
                ? Optional.of(aiStrategyFactory.create(aiDifficulty.orElseThrow()))
                : Optional.empty());

        startNewGameUseCase.execute(inputData);
    }

    public void restart() {
        startNewGameUseCase.execute(lastInput);
    }
}