package com.tictactoe.domain.ai;

import com.tictactoe.domain.Board;
import com.tictactoe.domain.GameConfig;
import com.tictactoe.domain.Mark;
import com.tictactoe.domain.Position;
import com.tictactoe.domain.WinChecker;
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
