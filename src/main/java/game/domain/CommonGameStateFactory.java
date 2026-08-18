package game.domain;

/**
 * Default {@link GameStateFactory} implementation, mirroring
 * {@code CommonUserFactory} in CAWithBuilder.
 */
public final class CommonGameStateFactory implements GameStateFactory {

    @Override
    public GameState newGame(GameConfig config) {
        return GameState.newGame(config);
    }
}