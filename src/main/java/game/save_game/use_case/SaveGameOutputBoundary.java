package game.save_game.use_case;

/**
 * The output boundary of the Save Game Use Case.
 */
public interface SaveGameOutputBoundary {

    void prepareSuccessView(SaveGameOutputData outputData);

    void prepareFailView(String error);
}