package com.tictactoe.domain.ai;

import com.tictactoe.domain.Board;
import com.tictactoe.domain.GameConfig;
import com.tictactoe.domain.LineGenerator;
import com.tictactoe.domain.LineGenerator.Line;
import com.tictactoe.domain.Mark;
import com.tictactoe.domain.Position;
import java.util.Optional;

public final class BoardEvaluator {

    private BoardEvaluator() {
    }

    public static long score(Board board, GameConfig config, Mark aiMark) {
        Mark opponent = aiMark.other();
        long total = 0;
        for (Line line : LineGenerator.allLines(config.boardSize(), config.winLength())) {
            total += scoreLine(board, line, aiMark, opponent);
        }
        return total;
    }

    private static long scoreLine(Board board, Line line, Mark aiMark, Mark opponent) {
        int aiCount = 0;
        int opponentCount = 0;
        for (Position position : line.positions()) {
            Optional<Mark> mark = board.get(position);
            if (mark.isEmpty()) {
                continue;
            }
            if (mark.get() == aiMark) {
                aiCount++;
            } else {
                opponentCount++;
            }
        }

        if (aiCount > 0 && opponentCount > 0) {
            return 0;
        }
        if (aiCount > 0) {
            return (long) Math.pow(10, aiCount);
        }
        if (opponentCount > 0) {
            return -(long) Math.pow(10, opponentCount);
        }
        return 0;
    }
}
