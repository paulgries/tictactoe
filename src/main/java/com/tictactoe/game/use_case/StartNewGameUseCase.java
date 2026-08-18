package com.tictactoe.game.use_case;

import com.tictactoe.game.NewGameRequest;
import com.tictactoe.game.domain.GameState;
import com.tictactoe.game.domain.GameStateFactory;

public final class StartNewGameUseCase {

    public GameState execute(NewGameRequest request) {
        return GameStateFactory.newGame(request.config());
    }
}
