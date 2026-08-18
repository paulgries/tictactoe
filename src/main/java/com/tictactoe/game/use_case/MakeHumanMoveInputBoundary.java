package com.tictactoe.game.use_case;

/**
 * Input boundary for the Make Human Move Use Case.
 */
public interface MakeHumanMoveInputBoundary {

    /**
     * Executes the make human move use case.
     * @param inputData the input data
     */
    void execute(MakeHumanMoveInputData inputData);
}