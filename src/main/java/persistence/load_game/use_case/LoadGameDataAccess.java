package persistence.load_game.use_case;

import game.domain.SavedGame;
import java.io.IOException;
import java.util.Optional;

/**
 * The persistence boundary of the Load Game Use Case. Implemented in the
 * outer layer (framework/storage), which owns the file format. An empty
 * result means no saved game exists.
 */
public interface LoadGameDataAccess {

    Optional<SavedGame> load() throws IOException;
}