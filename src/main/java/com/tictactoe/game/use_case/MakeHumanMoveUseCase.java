package com.tictactoe.game.use_case;

import com.tictactoe.game.domain.GameState;
import com.tictactoe.game.domain.Position;

public final class MakeHumanMoveUseCase {

    public GameState execute(GameState state, Position position) {
        return state.applyMove(position);
    }
}
