package game.save_game.use_case;

import game.domain.SavedGame;
import java.io.IOException;

/**
 * The persistence boundary of the Save Game Use Case. Implemented in the
 * outer layer (framework/storage), which owns the file format.
 */
public interface SaveGameDataAccess {

    void save(SavedGame savedGame) throws IOException;
}