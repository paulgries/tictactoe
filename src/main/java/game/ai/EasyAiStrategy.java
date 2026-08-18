package game.ai;

import game.domain.Board;
import game.domain.GameConfig;
import game.domain.Mark;
import game.domain.Position;
import game.domain.WinChecker;
import java.util.List;

public final class EasyAiStrategy implements AiStrategy {

    @Override
    public Position selectMove(Board board, GameConfig config, Mark aiMark) {
        List<Position> emptyPositions = board.emptyPositions();

        for (Position candidate : emptyPositions) {
            if (wins(board, config, candidate, aiMark)) {
                return candidate;
            }
        }

        Mark opponent = aiMark.other();
        for (Position candidate : emptyPositions) {
            if (wins(board, config, candidate, opponent)) {
                return candidate;
            }
        }

        int size = config.boardSize();
        if (size % 2 == 1) {
            Position center = new Position(size / 2, size / 2);
            if (emptyPositions.contains(center)) {
                return center;
            }
        }

        return emptyPositions.get(0);
    }

    private boolean wins(Board board, GameConfig config, Position candidate, Mark mark) {
        Board hypothetical = board.placeMark(candidate, mark);
        return WinChecker.findWinningLineThrough(hypothetical, config, candidate).isPresent();
    }
}
