package game.start_new_game.use_case;

import game.domain.GameStateFactory;

/**
 * The Interactor for the Start New Game Use Case.
 */
public final class StartNewGameInteractor implements StartNewGameInputBoundary {

    private final StartNewGameOutputBoundary presenter;

    public StartNewGameInteractor(StartNewGameOutputBoundary presenter) {
        this.presenter = presenter;
    }

    @Override
    public void execute(StartNewGameInputData inputData) {
        presenter.prepareSuccessView(new StartNewGameOutputData(
            GameStateFactory.newGame(inputData.config())));
    }
}