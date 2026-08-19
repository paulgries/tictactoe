package play.request_ai_move.use_case;

import game.domain.GameState;

/**
 * The output data for the Request AI Move Use Case. Echoes the base state
 * the move was computed from so the presenter can discard stale results
 * when the session has moved on (e.g. after a restart).
 */
public record RequestAiMoveOutputData(GameState updatedState, GameState base) {
}