package persistence.load_game.use_case;

import game.domain.SavedGame;

/**
 * The output data for the Load Game Use Case: the restored session.
 */
public record LoadGameOutputData(SavedGame savedGame) {
}