package com.tictactoe.adapters.viewmodel;

import com.tictactoe.domain.Position;

public record CellViewModel(Position position, CellSymbol symbol) {
}
