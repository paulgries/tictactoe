package setup.start_new_game.use_case;

/**
 * Input boundary for the Start New Game Use Case.
 */
public interface StartNewGameInputBoundary {

    /**
     * Executes the start new game use case.
     * @param inputData the input data
     */
    void execute(StartNewGameInputData inputData);

    /**
     * Switches to the setup view, as the CAWithBuilder signup boundary
     * switches to the login view.
     */
    void switchToSetupView();
}