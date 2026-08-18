package com.tictactoe.game.use_case;

import com.tictactoe.game.domain.GameState;

/**
 * The output data for the Make Human Move Use Case.
 */
public record MakeHumanMoveOutputData(GameState updatedState) {
}