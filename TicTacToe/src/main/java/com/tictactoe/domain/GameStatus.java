package com.tictactoe.domain;

public sealed interface GameStatus permits InProgress, Win, Draw {
}
