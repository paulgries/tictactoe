package game.make_human_move.use_case;

import game.domain.GameState;
import game.domain.Position;

/**
 * The input data for the Make Human Move Use Case.
 */
public record MakeHumanMoveInputData(GameState state, Position position) {
}