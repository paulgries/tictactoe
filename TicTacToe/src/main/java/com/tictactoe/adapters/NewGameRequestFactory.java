package com.tictactoe.adapters;

import com.tictactoe.application.NewGameRequest;
import com.tictactoe.domain.AiDifficulty;
import com.tictactoe.domain.GameConfig;
import com.tictactoe.domain.GameMode;
import java.util.Optional;

public final class NewGameRequestFactory {

    private NewGameRequestFactory() {
    }

    public static NewGameRequest create(
            int boardSize, int winLength, GameMode mode, Optional<AiDifficulty> aiDifficulty) {
        GameConfig config = new GameConfig(boardSize, winLength);
        return new NewGameRequest(config, mode, aiDifficulty);
    }
}
