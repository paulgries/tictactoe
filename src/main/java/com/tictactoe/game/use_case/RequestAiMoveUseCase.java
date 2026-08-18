package com.tictactoe.game.use_case;

import com.tictactoe.game.domain.GameState;
import com.tictactoe.game.domain.Position;
import com.tictactoe.game.ai.AiStrategy;
import com.tictactoe.game.domain.exception.InvalidMoveException;

public final class RequestAiMoveUseCase {

    public GameState execute(GameState state, AiStrategy strategy) {
        if (state.isGameOver()) {
            throw new InvalidMoveException("cannot request an AI move after the game is over");
        }

        Position move = strategy.selectMove(state.board(), state.config(), state.currentTurn());
        return state.applyMove(move);
    }
}
