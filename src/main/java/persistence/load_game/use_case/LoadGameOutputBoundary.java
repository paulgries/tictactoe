package persistence.load_game.use_case;

/**
 * The output boundary of the Load Game Use Case.
 */
public interface LoadGameOutputBoundary {

    void prepareSuccessView(LoadGameOutputData outputData);

    void prepareFailView(String error);
}