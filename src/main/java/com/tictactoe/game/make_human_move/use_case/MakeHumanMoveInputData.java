package com.tictactoe.game.make_human_move.use_case;

import com.tictactoe.game.domain.GameState;
import com.tictactoe.game.domain.Position;

/**
 * The input data for the Make Human Move Use Case.
 */
public record MakeHumanMoveInputData(GameState state, Position position) {
}