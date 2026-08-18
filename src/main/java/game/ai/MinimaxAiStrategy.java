package game.ai;

import game.domain.Board;
import game.domain.Draw;
import game.domain.GameConfig;
import game.domain.GameStatus;
import game.domain.Mark;
import game.domain.Position;
import game.domain.Win;
import game.domain.WinChecker;
import game.domain.exception.InvalidMoveException;
import java.util.List;

public final class MinimaxAiStrategy implements AiStrategy {

    static final int EXHAUSTIVE_EMPTY_CELL_THRESHOLD = 9;
    static final int MAX_DEPTH_LIMIT = 4;
    static final int MIN_DEPTH_LIMIT = 2;
    static final long NODE_BUDGET = 2_000_000L;
    private static final long WIN_SCORE = 1_000_000_000L;

    @Override
    public Position selectMove(Board board, GameConfig config, Mark aiMark) {
        List<Position> emptyPositions = board.emptyPositions();
        if (emptyPositions.isEmpty()) {
            throw new InvalidMoveException("no empty cells available");
        }

        int maxDepth = resolveMaxDepth(emptyPositions.size());

        Position bestMove = null;
        long bestScore = Long.MIN_VALUE;
        for (Position candidate : MoveOrderer.order(board, emptyPositions)) {
            Board next = board.placeMark(candidate, aiMark);
            long score = minimax(
                next, config, aiMark, aiMark.other(), 1, maxDepth, Long.MIN_VALUE, Long.MAX_VALUE);
            if (bestMove == null || score > bestScore) {
                bestScore = score;
                bestMove = candidate;
            }
        }
        return bestMove;
    }

    private long minimax(
            Board board, GameConfig config, Mark aiMark, Mark toMove,
            int depth, int maxDepth, long alpha, long beta) {
        GameStatus status = WinChecker.evaluate(board, config);
        if (status instanceof Win win) {
            return win.winner() == aiMark ? WIN_SCORE - depth : -(WIN_SCORE - depth);
        }
        if (status instanceof Draw) {
            return 0;
        }
        if (depth >= maxDepth) {
            return BoardEvaluator.score(board, config, aiMark);
        }

        boolean maximizing = toMove == aiMark;
        long best = maximizing ? Long.MIN_VALUE : Long.MAX_VALUE;
        for (Position candidate : MoveOrderer.order(board, board.emptyPositions())) {
            Board next = board.placeMark(candidate, toMove);
            long score = minimax(next, config, aiMark, toMove.other(), depth + 1, maxDepth, alpha, beta);
            if (maximizing) {
                best = Math.max(best, score);
                alpha = Math.max(alpha, best);
            } else {
                best = Math.min(best, score);
                beta = Math.min(beta, best);
            }
            if (alpha >= beta) {
                break;
            }
        }
        return best;
    }

    /**
     * Depth-limited search cost grows as roughly emptyCells^depth. A fixed depth that's
     * fast on a 3x3-6x6 board becomes computationally infeasible on larger boards, so the
     * depth is shrunk (down to a minimum of MIN_DEPTH_LIMIT) to keep the raw node count
     * under NODE_BUDGET regardless of board size.
     */
    static int resolveMaxDepth(int emptyCells) {
        if (emptyCells <= EXHAUSTIVE_EMPTY_CELL_THRESHOLD) {
            return Integer.MAX_VALUE;
        }
        int depth = MAX_DEPTH_LIMIT;
        while (depth > MIN_DEPTH_LIMIT && Math.pow(emptyCells, depth) > NODE_BUDGET) {
            depth--;
        }
        return depth;
    }
}
