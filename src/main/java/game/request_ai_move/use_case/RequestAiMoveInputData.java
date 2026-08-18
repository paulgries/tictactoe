package game.request_ai_move.use_case;

import game.ai.AiStrategy;
import game.domain.GameState;

/**
 * The input data for the Request AI Move Use Case.
 */
public record RequestAiMoveInputData(GameState state, AiStrategy strategy) {
}