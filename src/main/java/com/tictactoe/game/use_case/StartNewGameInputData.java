package com.tictactoe.game.use_case;

import com.tictactoe.game.NewGameRequest;

/**
 * The input data for the Start New Game Use Case.
 */
public record StartNewGameInputData(NewGameRequest request) {
}