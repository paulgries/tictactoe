package data_access;

import game.GameSessionDataAccess;
import game.domain.AiDifficulty;
import game.domain.GameMode;
import game.domain.GameState;
import java.util.Optional;

/**
 * The in-memory {@link GameSessionDataAccess}: holds the current game state
 * and its settings for the lifetime of the session, like CAWithBuilder's
 * {@code InMemoryUserDataAccessObject}.
 */
public final class InMemoryGameSession implements GameSessionDataAccess {

    private GameState currentGameState;
    private GameMode mode;
    private Optional<AiDifficulty> difficulty = Optional.empty();

    @Override
    public GameState getCurrentGameState() {
        return currentGameState;
    }

    @Override
    public GameMode getMode() {
        return mode;
    }

    @Override
    public Optional<AiDifficulty> getAiDifficulty() {
        return difficulty;
    }

    @Override
    public void setCurrentGame(GameState state, GameMode mode, Optional<AiDifficulty> difficulty) {
        this.currentGameState = state;
        this.mode = mode;
        this.difficulty = difficulty;
    }

    @Override
    public void setCurrentGameState(GameState state) {
        this.currentGameState = state;
    }
}