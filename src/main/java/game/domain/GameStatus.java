package game.domain;

public sealed interface GameStatus permits InProgress, Win, Draw {
}
