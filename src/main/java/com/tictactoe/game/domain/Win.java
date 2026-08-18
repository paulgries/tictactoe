package com.tictactoe.domain;

import java.util.List;

public record Win(Mark winner, List<Position> winningLine) implements GameStatus {
}
