package game.start_new_game.use_case;

import game.domain.GameConfig;
import game.domain.GameStateFactory;
import game.domain.exception.InvalidGameConfigException;

/**
 * The Interactor for the Start New Game Use Case. Builds a fresh game
 * through the injected {@link GameStateFactory} — as CAWithBuilder's
 * interactors receive their factories in the constructor. It records the
 * chosen AI difficulty but creates no strategy; the request-AI-move use
 * case builds its own strategy from the difficulty for each move.
 */
public final class StartNewGameInteractor implements StartNewGameInputBoundary {

    private final StartNewGameOutputBoundary presenter;
    private final GameStateFactory gameStateFactory;

    public StartNewGameInteractor(
            StartNewGameOutputBoundary presenter,
            GameStateFactory gameStateFactory) {
        this.presenter = presenter;
        this.gameStateFactory = gameStateFactory;
    }

    @Override
    public void execute(StartNewGameInputData inputData) {
        try {
            final GameConfig config = new GameConfig(inputData.boardSize(), inputData.winLength());
            presenter.prepareSuccessView(new StartNewGameOutputData(
                    gameStateFactory.newGame(config),
                    inputData.mode(),
                    inputData.aiDifficulty()));
        } catch (InvalidGameConfigException e) {
            presenter.prepareFailView(e.getMessage());
        }
    }

    @Override
    public void switchToSetupView() {
        presenter.switchToSetupView();
    }
}