package com.tictactoe.game.adapters;

import com.tictactoe.game.NewGameRequest;
import com.tictactoe.game.domain.AiDifficulty;
import com.tictactoe.game.domain.GameConfig;
import com.tictactoe.game.domain.GameMode;
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
