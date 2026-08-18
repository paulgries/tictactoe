package game.start_new_game.use_case;

import game.domain.AiDifficulty;
import game.domain.GameConfig;
import game.domain.GameMode;
import java.util.Optional;

/**
 * The input data for the Start New Game Use Case.
 */
public record StartNewGameInputData(
        GameConfig config, GameMode mode, Optional<AiDifficulty> aiDifficulty) {
}