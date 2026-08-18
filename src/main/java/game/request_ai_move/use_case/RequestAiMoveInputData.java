package game.request_ai_move.use_case;

import game.domain.AiDifficulty;
import game.domain.GameState;

/**
 * The input data for the Request AI Move Use Case: the state snapshot the
 * move is computed from and the difficulty that selects the strategy (the
 * interactor builds the strategy itself from its injected factory).
 */
public record RequestAiMoveInputData(GameState state, AiDifficulty difficulty) {
}