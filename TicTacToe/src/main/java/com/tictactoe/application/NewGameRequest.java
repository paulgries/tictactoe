package com.tictactoe.application;

import com.tictactoe.domain.AiDifficulty;
import com.tictactoe.domain.GameConfig;
import com.tictactoe.domain.GameMode;
import java.util.Optional;

public record NewGameRequest(GameConfig config, GameMode mode, Optional<AiDifficulty> aiDifficulty) {
}
