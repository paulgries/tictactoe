package com.tictactoe.game.make_human_move.use_case;

import com.tictactoe.game.domain.GameState;

/**
 * The output data for the Make Human Move Use Case.
 */
public record MakeHumanMoveOutputData(GameState updatedState) {
}