package play.make_human_move.use_case;

import game.domain.Position;

/**
 * The input data for the Make Human Move Use Case. The current game state is
 * read from the application-layer session by the interactor, so the
 * controller only forwards the clicked cell.
 */
public record MakeHumanMoveInputData(Position position) {
}