package game;

import game.BoardRenderState;
import game.CellSymbol;
import game.CellRenderState;
import game.GameOutcomeKind;
import game.StatusRenderState;
import game.domain.Draw;
import game.domain.GameState;
import game.domain.Mark;
import game.domain.Position;
import game.domain.Win;
import java.util.ArrayList;
import java.util.List;

public final class GameViewModelMapper {

    private GameViewModelMapper() {
    }

    public static BoardRenderState toBoardRenderState(GameState state) {
        int size = state.config().boardSize();
        List<CellRenderState> cells = new ArrayList<>(size * size);
        for (int row = 0; row < size; row++) {
            for (int column = 0; column < size; column++) {
                Position position = new Position(row, column);
                CellSymbol symbol = state.board().get(position)
                    .map(mark -> mark == Mark.X ? CellSymbol.X : CellSymbol.O)
                    .orElse(CellSymbol.EMPTY);
                cells.add(new CellRenderState(position, symbol));
            }
        }
        List<Position> winningLine = state.status() instanceof Win win
            ? win.winningLine()
            : List.of();
        return new BoardRenderState(size, cells, winningLine, outcomeKindOf(state));
    }

    public static StatusRenderState toStatusRenderState(GameState state) {
        String message;
        if (state.status() instanceof Win win) {
            message = win.winner() + " wins!";
        } else if (state.status() instanceof Draw) {
            message = "Draw!";
        } else {
            message = state.currentTurn() + "'s turn";
        }
        return new StatusRenderState(message, outcomeKindOf(state));
    }

    private static GameOutcomeKind outcomeKindOf(GameState state) {
        if (state.status() instanceof Win) {
            return GameOutcomeKind.WIN;
        }
        if (state.status() instanceof Draw) {
            return GameOutcomeKind.DRAW;
        }
        return GameOutcomeKind.IN_PROGRESS;
    }
}
