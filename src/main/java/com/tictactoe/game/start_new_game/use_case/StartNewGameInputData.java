package com.tictactoe.game.start_new_game.use_case;

import com.tictactoe.game.domain.AiDifficulty;
import com.tictactoe.game.domain.GameConfig;
import com.tictactoe.game.domain.GameMode;
import java.util.Optional;

/**
 * The input data for the Start New Game Use Case.
 */
public record StartNewGameInputData(
        GameConfig config, GameMode mode, Optional<AiDifficulty> aiDifficulty) {
}