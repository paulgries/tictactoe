package com.tictactoe.game.ai;

import com.tictactoe.game.domain.Board;
import com.tictactoe.game.domain.GameConfig;
import com.tictactoe.game.domain.Mark;
import com.tictactoe.game.domain.Position;
import com.tictactoe.game.domain.exception.InvalidMoveException;
import java.util.List;
import java.util.Random;

public final class RandomAiStrategy implements AiStrategy {

    private final Random random;

    public RandomAiStrategy(Random random) {
        this.random = random;
    }

    @Override
    public Position selectMove(Board board, GameConfig config, Mark aiMark) {
        List<Position> emptyPositions = board.emptyPositions();
        if (emptyPositions.isEmpty()) {
            throw new InvalidMoveException("no empty cells available");
        }
        return emptyPositions.get(random.nextInt(emptyPositions.size()));
    }
}
