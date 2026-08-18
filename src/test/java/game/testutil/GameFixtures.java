package com.tictactoe.game.testutil;

import com.tictactoe.game.domain.Board;
import com.tictactoe.game.domain.GameConfig;
import com.tictactoe.game.domain.GameState;
import com.tictactoe.game.domain.Mark;
import com.tictactoe.game.domain.Position;

/**
 * Reusable game fixtures for tests.
 */
public final class GameFixtures {

    private static final GameConfig CONFIG_3X3 = new GameConfig(3, 3);

    private GameFixtures() {
    }

    /** A 3x3 game won by X on the top row. */
    public static GameState wonByX() {
        return GameState.newGame(CONFIG_3X3)
            .applyMove(new Position(0, 0)) // X
            .applyMove(new Position(1, 0)) // O
            .applyMove(new Position(0, 1)) // X
            .applyMove(new Position(1, 1)) // O
            .applyMove(new Position(0, 2)); // X wins top row
    }

    /** A full 3x3 game with no winner.
     *
     * <pre>
     * X O X
     * X X O
     * O X O
     * </pre>
     */
    public static GameState drawn() {
        return GameState.newGame(CONFIG_3X3)
            .applyMove(new Position(0, 0)) // X
            .applyMove(new Position(0, 1)) // O
            .applyMove(new Position(0, 2)) // X
            .applyMove(new Position(1, 2)) // O
            .applyMove(new Position(1, 0)) // X
            .applyMove(new Position(2, 0)) // O
            .applyMove(new Position(1, 1)) // X
            .applyMove(new Position(2, 2)) // O
            .applyMove(new Position(2, 1)); // X
    }

    /** A 3x3 board won by X on the top row. */
    public static Board wonByXBoard() {
        return new Board(3)
            .placeMark(new Position(0, 0), Mark.X)
            .placeMark(new Position(0, 1), Mark.X)
            .placeMark(new Position(0, 2), Mark.X)
            .placeMark(new Position(1, 0), Mark.O)
            .placeMark(new Position(1, 1), Mark.O);
    }

    /** A 3x3 board won by O on the top row. */
    public static Board wonByOBoard() {
        return new Board(3)
            .placeMark(new Position(0, 0), Mark.O)
            .placeMark(new Position(0, 1), Mark.O)
            .placeMark(new Position(0, 2), Mark.O);
    }

    /** A full 3x3 board with no winner.
     *
     * <pre>
     * X O X
     * X X O
     * O X O
     * </pre>
     */
    public static Board drawnBoard() {
        return new Board(3)
            .placeMark(new Position(0, 0), Mark.X)
            .placeMark(new Position(0, 1), Mark.O)
            .placeMark(new Position(0, 2), Mark.X)
            .placeMark(new Position(1, 0), Mark.X)
            .placeMark(new Position(1, 1), Mark.X)
            .placeMark(new Position(1, 2), Mark.O)
            .placeMark(new Position(2, 0), Mark.O)
            .placeMark(new Position(2, 1), Mark.X)
            .placeMark(new Position(2, 2), Mark.O);
    }
}