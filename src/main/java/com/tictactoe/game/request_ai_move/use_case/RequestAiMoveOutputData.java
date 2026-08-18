package com.tictactoe.game.request_ai_move.use_case;

import com.tictactoe.game.domain.GameState;

/**
 * The output data for the Request AI Move Use Case.
 */
public record RequestAiMoveOutputData(GameState updatedState) {
}