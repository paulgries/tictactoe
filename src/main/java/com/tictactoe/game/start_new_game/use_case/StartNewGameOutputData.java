package com.tictactoe.game.start_new_game.use_case;

import com.tictactoe.game.domain.GameState;

/**
 * The output data for the Start New Game Use Case.
 */
public record StartNewGameOutputData(GameState gameState) {
}