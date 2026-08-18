package game.domain;

import java.util.Optional;

/**
 * A point-in-time snapshot of a session, persisted and restored by the
 * save/load use cases: the domain state plus the settings needed to resume
 * (game mode and, for AI games, the difficulty). The persistence format is
 * the store's business; the domain only defines the value.
 */
public record SavedGame(
        GameState gameState, GameMode mode, Optional<AiDifficulty> difficulty) {
}