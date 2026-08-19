package setup.start_new_game.use_case;

import game.GameSessionDataAccess;
import game.domain.GameConfig;
import game.domain.GameState;
import game.domain.GameStateFactory;
import game.domain.exception.InvalidGameConfigException;

/**
 * The Interactor for the Start New Game Use Case. Builds a fresh game
 * through the injected {@link GameStateFactory} — as CAWithBuilder's
 * interactors receive their factories in the constructor — and writes the
 * new session (state plus settings) to the application-layer session. It
 * records the chosen AI difficulty but creates no strategy; the
 * request-AI-move use case builds its own strategy from the difficulty for
 * each move.
 */
public final class StartNewGameInteractor implements StartNewGameInputBoundary {

    private final StartNewGameOutputBoundary presenter;
    private final GameStateFactory gameStateFactory;
    private final GameSessionDataAccess session;

    public StartNewGameInteractor(
            StartNewGameOutputBoundary presenter,
            GameStateFactory gameStateFactory,
            GameSessionDataAccess session) {
        this.presenter = presenter;
        this.gameStateFactory = gameStateFactory;
        this.session = session;
    }

    @Override
    public void execute(StartNewGameInputData inputData) {
        try {
            final GameConfig config = new GameConfig(inputData.boardSize(), inputData.winLength());
            final GameState gameState = gameStateFactory.newGame(config);
            session.setCurrentGame(gameState, inputData.mode(), inputData.aiDifficulty());
            presenter.prepareSuccessView(new StartNewGameOutputData(gameState));
        } catch (InvalidGameConfigException e) {
            presenter.prepareFailView(e.getMessage());
        }
    }

    @Override
    public void switchToSetupView() {
        presenter.switchToSetupView();
    }
}