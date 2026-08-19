package setup.start_new_game.use_case;

import game.domain.GameState;

/**
 * The output data for the Start New Game Use Case. Carries the fresh game
 * state the presenter renders; the session itself was written to the
 * application-layer session by the interactor.
 */
public record StartNewGameOutputData(GameState gameState) {
}