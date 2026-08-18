package com.tictactoe.game.use_case;

/**
 * Output boundary for the Make Human Move Use Case.
 */
public interface MakeHumanMoveOutputBoundary {

    /**
     * Prepares the success view for the Make Human Move Use Case.
     * @param outputData the output data
     */
    void prepareSuccessView(MakeHumanMoveOutputData outputData);

    /**
     * Prepares the failure view for the Make Human Move Use Case.
     * @param error the explanation of the failure
     */
    void prepareFailView(String error);
}