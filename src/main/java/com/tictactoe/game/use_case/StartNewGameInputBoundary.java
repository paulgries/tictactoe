package com.tictactoe.game.use_case;

/**
 * Input boundary for the Start New Game Use Case.
 */
public interface StartNewGameInputBoundary {

    /**
     * Executes the start new game use case.
     * @param inputData the input data
     */
    void execute(StartNewGameInputData inputData);
}