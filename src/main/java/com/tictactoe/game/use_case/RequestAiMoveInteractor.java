package com.tictactoe.game.use_case;

import com.tictactoe.game.domain.Position;
import com.tictactoe.game.domain.exception.InvalidMoveException;

/**
 * The Interactor for the Request AI Move Use Case.
 */
public final class RequestAiMoveInteractor implements RequestAiMoveInputBoundary {

    private final RequestAiMoveOutputBoundary presenter;

    public RequestAiMoveInteractor(RequestAiMoveOutputBoundary presenter) {
        this.presenter = presenter;
    }

    @Override
    public void execute(RequestAiMoveInputData inputData) {
        if (inputData.state().isGameOver()) {
            presenter.prepareFailView("cannot request an AI move after the game is over");
            return;
        }

        Position move = inputData.strategy()
            .selectMove(inputData.state().board(), inputData.state().config(), inputData.state().currentTurn());
        try {
            presenter.prepareSuccessView(new RequestAiMoveOutputData(inputData.state().applyMove(move)));
        } catch (InvalidMoveException e) {
            presenter.prepareFailView(e.getMessage());
        }
    }
}