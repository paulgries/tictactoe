package game.domain;

/**
 * Factory for building the {@link GameState} aggregate (the root of a
 * Tic-Tac-Toe match). Use cases receive an instance through their
 * constructor, as the {@code CommonUserFactory} is injected in
 * CAWithBuilder.
 */
public interface GameStateFactory {

    GameState newGame(GameConfig config);
}