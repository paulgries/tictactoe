package play.make_human_move.use_case;

import game.GameSessionDataAccess;
import game.GameSessionRules;
import game.domain.GameState;
import game.domain.exception.InvalidMoveException;

/**
 * The Interactor for the Make Human Move Use Case. Reads the current game
 * from the application-layer session, applies the move, writes the updated
 * game back, and reports whether the AI is to move next. Moves that cannot
 * apply (no game in progress, the game is over, or it is the AI's turn) are
 * ignored without touching the presenter.
 */
public final class MakeHumanMoveInteractor implements MakeHumanMoveInputBoundary {

    private final MakeHumanMoveOutputBoundary presenter;
    private final GameSessionDataAccess session;

    public MakeHumanMoveInteractor(
            MakeHumanMoveOutputBoundary presenter,
            GameSessionDataAccess session) {
        this.presenter = presenter;
        this.session = session;
    }

    @Override
    public void execute(MakeHumanMoveInputData inputData) {
        final GameState current = session.getCurrentGameState();
        if (current == null || current.isGameOver() || GameSessionRules.isAiTurn(session.getMode(), current)) {
            return;
        }
        try {
            final GameState updated = current.applyMove(inputData.position());
            session.setCurrentGameState(updated);
            presenter.prepareSuccessView(new MakeHumanMoveOutputData(
                    updated, GameSessionRules.isAiTurn(session.getMode(), updated)));
        } catch (InvalidMoveException e) {
            presenter.prepareFailView(e.getMessage());
        }
    }
}