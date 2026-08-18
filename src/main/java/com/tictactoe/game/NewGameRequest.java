package com.tictactoe.game;

import com.tictactoe.game.domain.AiDifficulty;
import com.tictactoe.game.domain.GameConfig;
import com.tictactoe.game.domain.GameMode;
import java.util.Optional;

public record NewGameRequest(GameConfig config, GameMode mode, Optional<AiDifficulty> aiDifficulty) {
}
