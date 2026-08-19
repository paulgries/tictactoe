package setup.start_new_game.use_case;

/**
 * Output boundary for the Start New Game Use Case.
 */
public interface StartNewGameOutputBoundary {

    /**
     * Prepares the success view for the Start New Game Use Case.
     * @param outputData the output data
     */
    void prepareSuccessView(StartNewGameOutputData outputData);

    /**
     * Prepares the failure view for the Start New Game Use Case.
     * @param error the explanation of the failure
     */
    void prepareFailView(String error);

    /**
     * Switches to the setup view, as the CAWithBuilder signup boundary
     * switches to the login view.
     */
    void switchToSetupView();
}