package game.start_new_game.use_case;

import game.ai.AiStrategy;
import game.ai.AiStrategyFactory;
import game.domain.GameConfig;
import game.domain.GameStateFactory;
import game.domain.exception.InvalidGameConfigException;
import java.util.Optional;

/**
 * The Interactor for the Start New Game Use Case. Builds a fresh game
 * through the injected {@link GameStateFactory} and, for AI games, the AI
 * strategy through the injected {@link AiStrategyFactory} — as
 * CAWithBuilder's interactors receive their factories in the constructor.
 */
public final class StartNewGameInteractor implements StartNewGameInputBoundary {

    private final StartNewGameOutputBoundary presenter;
    private final GameStateFactory gameStateFactory;
    private final AiStrategyFactory aiStrategyFactory;

    public StartNewGameInteractor(
            StartNewGameOutputBoundary presenter,
            GameStateFactory gameStateFactory,
            AiStrategyFactory aiStrategyFactory) {
        this.presenter = presenter;
        this.gameStateFactory = gameStateFactory;
        this.aiStrategyFactory = aiStrategyFactory;
    }

    @Override
    public void execute(StartNewGameInputData inputData) {
        final Optional<AiStrategy> aiStrategy = inputData.aiDifficulty()
                .map(aiStrategyFactory::create);
        try {
            final GameConfig config = new GameConfig(inputData.boardSize(), inputData.winLength());
            presenter.prepareSuccessView(new StartNewGameOutputData(
                    gameStateFactory.newGame(config),
                    inputData.mode(),
                    aiStrategy));
        } catch (InvalidGameConfigException e) {
            presenter.prepareFailView(e.getMessage());
        }
    }

    @Override
    public void switchToSetupView() {
        presenter.switchToSetupView();
    }
}