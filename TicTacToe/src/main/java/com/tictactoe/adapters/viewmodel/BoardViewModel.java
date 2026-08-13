package com.tictactoe.adapters.viewmodel;

import com.tictactoe.domain.Position;
import java.util.List;

public record BoardViewModel(
    int size, List<CellViewModel> cells, List<Position> winningLine, GameOutcomeKind outcome) {
}
