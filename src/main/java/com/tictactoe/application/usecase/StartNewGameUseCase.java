package com.tictactoe.application.usecase;

import com.tictactoe.application.NewGameRequest;
import com.tictactoe.domain.GameState;

public final class StartNewGameUseCase {

    public GameState execute(NewGameRequest request) {
        return GameState.newGame(request.config());
    }
}
