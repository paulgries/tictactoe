package game.ai;

import static org.assertj.core.api.Assertions.assertThat;

import game.domain.Board;
import game.domain.GameConfig;
import game.domain.GameState;
import game.domain.Mark;
import game.domain.Position;
import game.domain.Win;
import org.junit.jupiter.api.Test;

class MinimaxAiStrategyTest {

    private static final GameConfig CONFIG_3X3 = new GameConfig(3, 3);
    private final MinimaxAiStrategy strategy = new MinimaxAiStrategy();

    @Test
    void selectsImmediateWinningMove_3x3() {
        GameState state = GameState.newGame(CONFIG_3X3)
            .applyMove(new Position(0, 0)) // X
            .applyMove(new Position(1, 0)) // O
            .applyMove(new Position(0, 1)) // X
            .applyMove(new Position(1, 1)); // O

        Position move = strategy.selectMove(state.board(), state.config(), state.currentTurn());

        assertThat(move).isEqualTo(new Position(0, 2));
    }

    @Test
    void blocksOpponentsImmediateWin_3x3() {
        GameState state = GameState.newGame(CONFIG_3X3)
            .applyMove(new Position(2, 2)) // X
            .applyMove(new Position(0, 0)) // O
            .applyMove(new Position(1, 1)) // X
            .applyMove(new Position(0, 1)); // O -> threatens (0,2)

        Position move = strategy.selectMove(state.board(), state.config(), state.currentTurn());

        assertThat(move).isEqualTo(new Position(0, 2));
    }

    @Test
    void winsWhenOpponentMakesASuboptimalMove_3x3() {
        GameState state = GameState.newGame(CONFIG_3X3);

        while (!state.isGameOver()) {
            Position move = state.currentTurn() == Mark.X
                ? strategy.selectMove(state.board(), state.config(), Mark.X)
                : state.board().emptyPositions().get(0); // naive: always first empty cell
            state = state.applyMove(move);
        }

        assertThat(state.status()).isInstanceOf(Win.class);
        assertThat(((Win) state.status()).winner()).isEqualTo(Mark.X);
    }

    @Test
    void resultsInDrawWhenBothSidesPlayOptimally_3x3() {
        GameState state = GameState.newGame(CONFIG_3X3);

        while (!state.isGameOver()) {
            Position move = strategy.selectMove(state.board(), state.config(), state.currentTurn());
            state = state.applyMove(move);
        }

        assertThat(state.status()).isInstanceOf(game.domain.Draw.class);
    }

    @Test
    void neverLosesAcrossAllPossibleOpponentFirstMoves_3x3() {
        for (int row = 0; row < 3; row++) {
            for (int column = 0; column < 3; column++) {
                GameState state = GameState.newGame(CONFIG_3X3)
                    .applyMove(new Position(row, column)); // X opens

                while (!state.isGameOver()) {
                    Position move = state.currentTurn() == Mark.O
                        ? strategy.selectMove(state.board(), state.config(), Mark.O)
                        : state.board().emptyPositions().get(0); // naive X continuation
                    state = state.applyMove(move);
                }

                if (state.status() instanceof Win win) {
                    assertThat(win.winner())
                        .as("minimax (O) should never lose after X opens at (%d,%d)", row, column)
                        .isNotEqualTo(Mark.X);
                }
            }
        }
    }

    @Test
    void respondsWithinBoundedTimeOnLargerBoard_6x6_winLength4() {
        GameConfig config = new GameConfig(6, 4);
        Board board = new Board(6);

        long startNanos = System.nanoTime();
        Position move = strategy.selectMove(board, config, Mark.X);
        long elapsedMillis = (System.nanoTime() - startNanos) / 1_000_000;

        assertThat(board.emptyPositions()).contains(move);
        assertThat(elapsedMillis).isLessThan(10_000);
    }

    @Test
    void respondsWithinBoundedTimeOnVeryLargeBoard_9x9_winLength4() {
        GameConfig config = new GameConfig(9, 4);
        Board board = new Board(9);

        long startNanos = System.nanoTime();
        Position move = strategy.selectMove(board, config, Mark.X);
        long elapsedMillis = (System.nanoTime() - startNanos) / 1_000_000;

        assertThat(board.emptyPositions()).contains(move);
        assertThat(elapsedMillis).isLessThan(10_000);
    }

    @Test
    void neverReturnsOccupiedOrOutOfBoundsPosition() {
        Board board = new Board(3)
            .placeMark(new Position(1, 1), Mark.X)
            .placeMark(new Position(0, 0), Mark.O);

        Position move = strategy.selectMove(board, CONFIG_3X3, Mark.X);

        assertThat(board.emptyPositions()).contains(move);
    }

    @Test
    void resolveMaxDepthIsExhaustiveAtOrBelowTheEndgameThreshold() {
        assertThat(MinimaxAiStrategy.resolveMaxDepth(9)).isEqualTo(Integer.MAX_VALUE);
        assertThat(MinimaxAiStrategy.resolveMaxDepth(1)).isEqualTo(Integer.MAX_VALUE);
    }

    @Test
    void resolveMaxDepthStaysAtTheFullLimitWhenTheNodeBudgetAllowsIt() {
        assertThat(MinimaxAiStrategy.resolveMaxDepth(36)).isEqualTo(4); // 6x6: 36^4 fits the budget
    }

    @Test
    void resolveMaxDepthShrinksToStayWithinTheNodeBudgetOnLargerBoards() {
        assertThat(MinimaxAiStrategy.resolveMaxDepth(81)).isEqualTo(3); // 9x9: 81^4 exceeds the budget
    }

    @Test
    void resolveMaxDepthNeverGoesBelowTheMinimum() {
        assertThat(MinimaxAiStrategy.resolveMaxDepth(10_000)).isEqualTo(2);
    }
}
