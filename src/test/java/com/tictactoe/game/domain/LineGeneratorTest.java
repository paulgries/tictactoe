package com.tictactoe.game.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.tictactoe.game.domain.LineGenerator.Line;
import java.util.List;
import org.junit.jupiter.api.Test;

class LineGeneratorTest {

    @Test
    void allLinesFor3x3WinLength3ReturnsEightClassicLines() {
        List<Line> lines = LineGenerator.allLines(3, 3);

        assertThat(lines).hasSize(8);
    }

    @Test
    void allLinesCountForLargerBoardWithSmallerWinLengthMatchesFormula() {
        // 5x5 board, winLength 3: horizontal 5*3=15, vertical 5*3=15,
        // each diagonal family 3*3=9 -> total 15+15+9+9=48
        List<Line> lines = LineGenerator.allLines(5, 3);

        assertThat(lines).hasSize(48);
    }

    @Test
    void everyLineHasExactlyWinLengthPositionsWithinBoardBounds() {
        int boardSize = 5;
        int winLength = 3;
        List<Line> lines = LineGenerator.allLines(boardSize, winLength);

        assertThat(lines).isNotEmpty();
        for (Line line : lines) {
            assertThat(line.positions()).hasSize(winLength);
            for (Position position : line.positions()) {
                assertThat(position.row()).isBetween(0, boardSize - 1);
                assertThat(position.column()).isBetween(0, boardSize - 1);
            }
        }
    }
}
