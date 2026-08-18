package com.tictactoe.game.adapters.viewmodel;

import com.tictactoe.game.domain.Position;

public record CellViewModel(Position position, CellSymbol symbol) {
}
