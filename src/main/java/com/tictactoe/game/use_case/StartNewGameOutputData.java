package com.tictactoe.game.use_case;

import com.tictactoe.game.domain.GameState;

/**
 * The output data for the Start New Game Use Case.
 */
public record StartNewGameOutputData(GameState gameState) {
}