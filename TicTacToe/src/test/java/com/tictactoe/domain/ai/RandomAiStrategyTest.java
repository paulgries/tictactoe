package com.tictactoe.domain.ai;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.tictactoe.domain.Board;
import com.tictactoe.domain.GameConfig;
import com.tictactoe.domain.Mark;
import com.tictactoe.domain.Position;
import com.tictactoe.domain.exception.InvalidMoveException;
import java.util.Random;
import org.junit.jupiter.api.Test;

class RandomAiStrategyTest {

    private static final GameConfig CONFIG_3X3 = new GameConfig(3, 3);

    @Test
    void alwaysReturnsAnEmptyLegalPosition() {
        Board board = new Board(3)
            .placeMark(new Position(0, 0), Mark.X)
            .placeMark(new Position(1, 1), Mark.O);
        RandomAiStrategy strategy = new RandomAiStrategy(new Random());

        for (int i = 0; i < 150; i++) {
            Position move = strategy.selectMove(board, CONFIG_3X3, Mark.X);
            assertThat(board.emptyPositions()).contains(move);
        }
    }

    @Test
    void isDeterministicGivenSameSeed() {
        Board board = new Board(3).placeMark(new Position(0, 0), Mark.X);

        RandomAiStrategy strategyA = new RandomAiStrategy(new Random(42));
        RandomAiStrategy strategyB = new RandomAiStrategy(new Random(42));

        Position moveA = strategyA.selectMove(board, CONFIG_3X3, Mark.O);
        Position moveB = strategyB.selectMove(board, CONFIG_3X3, Mark.O);

        assertThat(moveA).isEqualTo(moveB);
    }

    @Test
    void throwsWhenBoardHasNoEmptyCells() {
        Board board = new Board(2)
            .placeMark(new Position(0, 0), Mark.X)
            .placeMark(new Position(0, 1), Mark.O)
            .placeMark(new Position(1, 0), Mark.O)
            .placeMark(new Position(1, 1), Mark.X);
        RandomAiStrategy strategy = new RandomAiStrategy(new Random());

        assertThatThrownBy(() -> strategy.selectMove(board, new GameConfig(2, 2), Mark.X))
            .isInstanceOf(InvalidMoveException.class);
    }
}
