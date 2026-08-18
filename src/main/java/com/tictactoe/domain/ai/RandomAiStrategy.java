package com.tictactoe.domain.ai;

import com.tictactoe.domain.Board;
import com.tictactoe.domain.GameConfig;
import com.tictactoe.domain.Mark;
import com.tictactoe.domain.Position;
import com.tictactoe.domain.exception.InvalidMoveException;
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
