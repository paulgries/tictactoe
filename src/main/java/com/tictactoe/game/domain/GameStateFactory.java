package com.tictactoe.game.domain;

/**
 * Factory for building the {@link GameState} aggregate (the root of a Tic-Tac-Toe
 * match). Controllers and use cases obtain fresh states through this factory rather
 * than constructing the aggregate directly.
 */
public final class GameStateFactory {

    private GameStateFactory() {
    }

    public static GameState newGame(GameConfig config) {
        return new GameState(
            new Board(config.boardSize()),
            GameState.STARTING_MARK,
            config,
            new InProgress());
    }
}