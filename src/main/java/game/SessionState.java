package game;

import game.ai.AiStrategy;
import game.domain.GameMode;
import java.util.Optional;

/**
 * The session part of the game view model: the current domain state plus the
 * settings that controllers and presenters share across use cases. Presenters
 * write it; controllers snapshot it. It holds no render data and no game
 * policy.
 */
public class SessionState {

    private game.domain.GameState currentGameState;
    private GameMode mode;
    private Optional<AiStrategy> aiStrategy = Optional.empty();

    public game.domain.GameState getCurrentGameState() {
        return currentGameState;
    }

    public void setCurrentGameState(game.domain.GameState currentGameState) {
        this.currentGameState = currentGameState;
    }

    public GameMode getMode() {
        return mode;
    }

    public void setMode(GameMode mode) {
        this.mode = mode;
    }

    public Optional<AiStrategy> getAiStrategy() {
        return aiStrategy;
    }

    public void setAiStrategy(Optional<AiStrategy> aiStrategy) {
        this.aiStrategy = aiStrategy;
    }
}