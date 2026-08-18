package game.domain;

import game.domain.LineGenerator.Line;
import java.util.List;
import java.util.Optional;

public final class WinChecker {

    private WinChecker() {
    }

    public static GameStatus evaluate(Board board, GameConfig config) {
        for (Line line : LineGenerator.allLines(config.boardSize(), config.winLength())) {
            Optional<Mark> winner = winnerOf(board, line);
            if (winner.isPresent()) {
                return new Win(winner.get(), line.positions());
            }
        }
        if (board.isFull()) {
            return new Draw();
        }
        return new InProgress();
    }

    public static Optional<List<Position>> findWinningLineThrough(
            Board board, GameConfig config, Position position) {
        Optional<Mark> mark = board.get(position);
        if (mark.isEmpty()) {
            return Optional.empty();
        }

        for (Line line : LineGenerator.allLines(config.boardSize(), config.winLength())) {
            if (!line.positions().contains(position)) {
                continue;
            }
            Optional<Mark> winner = winnerOf(board, line);
            if (winner.isPresent() && winner.get() == mark.get()) {
                return Optional.of(line.positions());
            }
        }
        return Optional.empty();
    }

    private static Optional<Mark> winnerOf(Board board, Line line) {
        Mark first = null;
        for (Position position : line.positions()) {
            Optional<Mark> mark = board.get(position);
            if (mark.isEmpty()) {
                return Optional.empty();
            }
            if (first == null) {
                first = mark.get();
            } else if (first != mark.get()) {
                return Optional.empty();
            }
        }
        return Optional.ofNullable(first);
    }
}
