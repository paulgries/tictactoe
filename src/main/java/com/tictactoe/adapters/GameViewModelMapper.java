package com.tictactoe.adapters;

import com.tictactoe.adapters.viewmodel.BoardViewModel;
import com.tictactoe.adapters.viewmodel.CellSymbol;
import com.tictactoe.adapters.viewmodel.CellViewModel;
import com.tictactoe.adapters.viewmodel.GameOutcomeKind;
import com.tictactoe.adapters.viewmodel.StatusViewModel;
import com.tictactoe.domain.Draw;
import com.tictactoe.domain.GameState;
import com.tictactoe.domain.Mark;
import com.tictactoe.domain.Position;
import com.tictactoe.domain.Win;
import java.util.ArrayList;
import java.util.List;

public final class GameViewModelMapper {

    private GameViewModelMapper() {
    }

    public static BoardViewModel toBoardViewModel(GameState state) {
        int size = state.config().boardSize();
        List<CellViewModel> cells = new ArrayList<>(size * size);
        for (int row = 0; row < size; row++) {
            for (int column = 0; column < size; column++) {
                Position position = new Position(row, column);
                CellSymbol symbol = state.board().get(position)
                    .map(mark -> mark == Mark.X ? CellSymbol.X : CellSymbol.O)
                    .orElse(CellSymbol.EMPTY);
                cells.add(new CellViewModel(position, symbol));
            }
        }
        List<Position> winningLine = state.status() instanceof Win win
            ? win.winningLine()
            : List.of();
        return new BoardViewModel(size, cells, winningLine, outcomeKindOf(state));
    }

    public static StatusViewModel toStatusViewModel(GameState state) {
        String message;
        if (state.status() instanceof Win win) {
            message = win.winner() + " wins!";
        } else if (state.status() instanceof Draw) {
            message = "Draw!";
        } else {
            message = state.currentTurn() + "'s turn";
        }
        return new StatusViewModel(message, outcomeKindOf(state));
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
