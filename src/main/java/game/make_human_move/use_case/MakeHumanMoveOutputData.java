package game.make_human_move.use_case;

import game.domain.GameState;

/**
 * The output data for the Make Human Move Use Case. Carries the updated
 * state for the presenter to render and whether the AI is to move next, so
 * the presenter can hand off to the AI move request.
 */
public record MakeHumanMoveOutputData(GameState updatedState, boolean aiToMoveNext) {
}