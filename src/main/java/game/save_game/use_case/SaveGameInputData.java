package game.save_game.use_case;

import game.domain.AiDifficulty;
import game.domain.GameMode;
import game.domain.GameState;
import java.util.Optional;

/**
 * The input data for the Save Game Use Case: the current session, which the
 * controller snapshots from the shared view model.
 */
public record SaveGameInputData(
        GameState gameState, GameMode mode, Optional<AiDifficulty> difficulty) {
}