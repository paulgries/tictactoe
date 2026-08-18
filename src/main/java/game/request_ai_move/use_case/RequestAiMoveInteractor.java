package game.request_ai_move.use_case;

import game.ai.AiStrategy;
import game.ai.AiStrategyFactory;
import game.domain.GameState;
import game.domain.Position;
import game.domain.exception.InvalidMoveException;

/**
 * The Interactor for the Request AI Move Use Case. Builds the AI strategy
 * from the requested difficulty through the injected {@link AiStrategyFactory}
 * and applies the move to the snapshot it is given.
 */
public final class RequestAiMoveInteractor implements RequestAiMoveInputBoundary {

    private final RequestAiMoveOutputBoundary presenter;
    private final AiStrategyFactory aiStrategyFactory;

    public RequestAiMoveInteractor(
            RequestAiMoveOutputBoundary presenter,
            AiStrategyFactory aiStrategyFactory) {
        this.presenter = presenter;
        this.aiStrategyFactory = aiStrategyFactory;
    }

    @Override
    public void execute(RequestAiMoveInputData inputData) {
        if (inputData.state().isGameOver()) {
            presenter.prepareFailView("cannot request an AI move after the game is over");
            return;
        }

        final AiStrategy strategy = aiStrategyFactory.create(inputData.difficulty());
        Position move = strategy
            .selectMove(inputData.state().board(), inputData.state().config(), inputData.state().currentTurn());
        try {
            GameState updated = inputData.state().applyMove(move);
            presenter.prepareSuccessView(new RequestAiMoveOutputData(updated, inputData.state()));
        } catch (InvalidMoveException e) {
            presenter.prepareFailView(e.getMessage());
        }
    }
}