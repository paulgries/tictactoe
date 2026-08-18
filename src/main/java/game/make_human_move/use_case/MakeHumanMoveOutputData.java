package game.make_human_move.use_case;

import game.domain.GameState;

/**
 * The output data for the Make Human Move Use Case.
 */
public record MakeHumanMoveOutputData(GameState updatedState) {
}