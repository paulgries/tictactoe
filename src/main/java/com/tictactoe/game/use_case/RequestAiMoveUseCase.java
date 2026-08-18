package com.tictactoe.application.usecase;

import com.tictactoe.domain.GameState;
import com.tictactoe.domain.Position;
import com.tictactoe.domain.ai.AiStrategy;
import com.tictactoe.domain.exception.InvalidMoveException;

public final class RequestAiMoveUseCase {

    public GameState execute(GameState state, AiStrategy strategy) {
        if (state.isGameOver()) {
            throw new InvalidMoveException("cannot request an AI move after the game is over");
        }

        Position move = strategy.selectMove(state.board(), state.config(), state.currentTurn());
        return state.applyMove(move);
    }
}
