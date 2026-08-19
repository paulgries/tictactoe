package persistence.save_game.use_case;

/**
 * The input boundary of the Save Game Use Case.
 */
public interface SaveGameInputBoundary {

    void execute(SaveGameInputData inputData);
}