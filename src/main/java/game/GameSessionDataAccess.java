package game;

import game.domain.AiDifficulty;
import game.domain.GameMode;
import game.domain.GameState;
import java.util.Optional;

/**
 * The application-layer session: the game in progress plus its settings,
 * shared by the use cases that operate on the current game. The concrete
 * in-memory implementation lives in {@code data_access}, mirroring
 * CAWithBuilder's {@code InMemoryUserDataAccessObject}.
 */
public interface GameSessionDataAccess {

    GameState getCurrentGameState();

    GameMode getMode();

    Optional<AiDifficulty> getAiDifficulty();

    void setCurrentGame(GameState state, GameMode mode, Optional<AiDifficulty> difficulty);

    void setCurrentGameState(GameState state);
}