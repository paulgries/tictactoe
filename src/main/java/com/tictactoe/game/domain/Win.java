package com.tictactoe.game.domain;

import java.util.List;

public record Win(Mark winner, List<Position> winningLine) implements GameStatus {
}
