package game.request_ai_move.use_case;

import game.domain.GameState;

/**
 * The output data for the Request AI Move Use Case.
 */
public record RequestAiMoveOutputData(GameState updatedState) {
}