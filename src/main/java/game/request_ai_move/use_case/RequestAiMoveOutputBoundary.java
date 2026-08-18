package game.request_ai_move.use_case;

/**
 * Output boundary for the Request AI Move Use Case.
 */
public interface RequestAiMoveOutputBoundary {

    /**
     * Prepares the success view for the Request AI Move Use Case.
     * @param outputData the output data
     */
    void prepareSuccessView(RequestAiMoveOutputData outputData);

    /**
     * Prepares the failure view for the Request AI Move Use Case.
     * @param error the explanation of the failure
     */
    void prepareFailView(String error);
}