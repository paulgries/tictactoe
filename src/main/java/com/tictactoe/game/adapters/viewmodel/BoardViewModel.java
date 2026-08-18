package com.tictactoe.game.adapters.viewmodel;

import com.tictactoe.game.domain.Position;
import java.util.List;

public record BoardViewModel(
    int size, List<CellViewModel> cells, List<Position> winningLine, GameOutcomeKind outcome) {
}
