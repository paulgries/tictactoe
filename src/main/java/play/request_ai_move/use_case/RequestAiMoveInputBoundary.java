package play.request_ai_move.use_case;

/**
 * Input boundary for the Request AI Move Use Case.
 */
public interface RequestAiMoveInputBoundary {

    /**
     * Executes the request AI move use case.
     * @param inputData the input data
     */
    void execute(RequestAiMoveInputData inputData);
}