package com.tictactoe.game.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class LineGenerator {

    private static final Map<LineKey, List<Line>> CACHE = new ConcurrentHashMap<>();

    private LineGenerator() {
    }

    public record Line(List<Position> positions) {
    }

    private record LineKey(int boardSize, int winLength) {
    }

    /**
     * boardSize/winLength never change mid-search, but this is called at every node of
     * MinimaxAiStrategy's tree search (up to NODE_BUDGET times per AI move), so the
     * result is memoized rather than rebuilt from scratch on every call.
     */
    public static List<Line> allLines(int boardSize, int winLength) {
        return CACHE.computeIfAbsent(new LineKey(boardSize, winLength), LineGenerator::computeAllLines);
    }

    private static List<Line> computeAllLines(LineKey key) {
        int boardSize = key.boardSize();
        int winLength = key.winLength();
        List<Line> lines = new ArrayList<>();
        int lastStart = boardSize - winLength;

        // horizontal
        for (int row = 0; row < boardSize; row++) {
            for (int startColumn = 0; startColumn <= lastStart; startColumn++) {
                lines.add(line(row, startColumn, 0, 1, winLength));
            }
        }

        // vertical
        for (int column = 0; column < boardSize; column++) {
            for (int startRow = 0; startRow <= lastStart; startRow++) {
                lines.add(line(startRow, column, 1, 0, winLength));
            }
        }

        // diagonal: top-left to bottom-right
        for (int startRow = 0; startRow <= lastStart; startRow++) {
            for (int startColumn = 0; startColumn <= lastStart; startColumn++) {
                lines.add(line(startRow, startColumn, 1, 1, winLength));
            }
        }

        // diagonal: top-right to bottom-left
        for (int startRow = 0; startRow <= lastStart; startRow++) {
            for (int startColumn = winLength - 1; startColumn < boardSize; startColumn++) {
                lines.add(line(startRow, startColumn, 1, -1, winLength));
            }
        }

        return List.copyOf(lines);
    }

    private static Line line(int startRow, int startColumn, int rowStep, int columnStep, int winLength) {
        List<Position> positions = new ArrayList<>(winLength);
        for (int i = 0; i < winLength; i++) {
            positions.add(new Position(startRow + i * rowStep, startColumn + i * columnStep));
        }
        return new Line(positions);
    }
}
