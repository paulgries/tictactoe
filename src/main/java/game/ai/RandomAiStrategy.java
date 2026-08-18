package game.ai;

import game.domain.Board;
import game.domain.GameConfig;
import game.domain.Mark;
import game.domain.Position;
import game.domain.exception.InvalidMoveException;
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
