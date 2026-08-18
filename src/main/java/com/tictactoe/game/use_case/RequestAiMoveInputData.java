package com.tictactoe.game.use_case;

import com.tictactoe.game.ai.AiStrategy;
import com.tictactoe.game.domain.GameState;

/**
 * The input data for the Request AI Move Use Case.
 */
public record RequestAiMoveInputData(GameState state, AiStrategy strategy) {
}