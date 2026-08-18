package game.start_new_game.use_case;

import game.ai.AiStrategy;
import game.domain.GameMode;
import game.domain.GameState;
import java.util.Optional;

/**
 * The output data for the Start New Game Use Case. Carries the fresh game
 * state plus the session data (mode and AI strategy) the presenter stashes
 * in the shared view model.
 */
public record StartNewGameOutputData(
        GameState gameState, GameMode mode, Optional<AiStrategy> aiStrategy) {
}