package com.tictactoe.application.usecase;

import com.tictactoe.domain.GameState;
import com.tictactoe.domain.Position;

public final class MakeHumanMoveUseCase {

    public GameState execute(GameState state, Position position) {
        return state.applyMove(position);
    }
}
